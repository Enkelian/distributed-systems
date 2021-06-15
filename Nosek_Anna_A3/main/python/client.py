import sys, Ice, os, pause
from datetime import datetime
from TheOffice import Request, RequestType, OfficePrx
from request_manager import RequestManager, TYPES
from threading import Event, Thread

TYPE_PROXY = {
    RequestType.ID: 'document',
    RequestType.PASSPORT: 'document',
    RequestType.LICENSE: 'document',
    RequestType.RESIDENTREGISTRATION: 'resident'
}

exit_event = Event()

REQUEST_DIR = f'{os.path.dirname(__file__)}{os.sep}..{os.sep}resources{os.sep}requests'


def get_request_location(request_id):
    return f'{REQUEST_DIR}{os.sep}{request_id}'


class Client:
    def __init__(self, args):
        self.args = args
        self.proxies = {}
        self.communicator = None

    def run(self):
        self.communicator = Ice.initialize(self.args)

        while True:
            option = input('N - new request, S - check status of request, E - exit: ')
            if option == "N":
                self.create_request()
            elif option == "S":
                self.check_request()
            elif option == "E":
                self.communicator.destroy()
                exit_event.set()
                break

    def get_proxy(self, request_type):
        category = TYPE_PROXY[request_type]

        office_obj = None

        if category in self.proxies:
            office_obj = self.proxies[category]

        if office_obj is None:
            office_obj = OfficePrx.checkedCast(self.communicator.stringToProxy(f'office/{category}@Adapter1'))
            self.proxies[category] = office_obj

        if not office_obj:
            raise RuntimeError("Invalid proxy")

        return office_obj

    def create_request(self):
        request_type = RequestManager.get_request_type()

        office_obj = self.get_proxy(request_type)

        if request_type in [RequestType.ID, RequestType.PASSPORT]:
            data = RequestManager.get_document_data()
        elif request_type == RequestType.LICENSE:
            data = RequestManager.get_license_data()
        elif request_type == RequestType.RESIDENTREGISTRATION:
            data = RequestManager.get_residence_data()

        immediate_response = office_obj.sendRequest(Request(request_type, data))

        with open(get_request_location(immediate_response.requestID), 'w+') as request_file:
            dt = immediate_response.expectedResponseDateTime

            request_file.write(f'{request_type},{dt.date.year},{dt.date.month},{dt.date.day}'
                               f',{dt.time.hour},{dt.time.minute},{dt.time.second}')

        print(f'Your ID is {immediate_response.requestID}. '
              f'Estimated time is {immediate_response.expectedResponseDateTime}')

    def wait_for_response(self, dt, request_type, request_id):

        if dt > datetime.now():
            print('Waiting for completion time')
            exit_event.wait((dt - datetime.now()).total_seconds())

        if exit_event.isSet():
            return

        office_obj = self.get_proxy(TYPES[request_type])

        result = office_obj.getResult(request_id)

        print(result)
        os.remove(get_request_location(request_id))

    def check_request(self):

        request_id = RequestManager.get_request_id()

        try:
            with open(get_request_location(request_id)) as f:

                file_contents = f.read().split(',')
                request_type = file_contents[0]

                dt = datetime(*list(map(int, file_contents[1:])))

                thread = Thread(target=self.wait_for_response, args=(dt, request_type, request_id))
                thread.start()

                # office_obj = self.get_proxy(TYPES[request_type])
                #
                # if dt > datetime.now():
                #     print('Waiting for completion time')
                # pause.until(dt)
                #
                # result = office_obj.getResult(request_id)
                #
                # print(result)
            # os.remove(get_request_location(request_id))
        except IOError:
            print('No such request')

Client(sys.argv).run()
