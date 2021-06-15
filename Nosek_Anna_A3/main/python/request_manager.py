from TheOffice import DocumentData, Address, ResidentRegistrationData, RequestType, LicenseData

TYPES = {
    'ID': RequestType.ID,
    'LICENSE': RequestType.LICENSE,
    'PASSPORT': RequestType.PASSPORT,
    'RESIDENTREGISTRATION': RequestType.RESIDENTREGISTRATION
}


class RequestManager:

    @staticmethod
    def get_document_data():
        name = input('Name: ')
        surname = input('Surname: ')
        pesel = input('PESEL: ')
        return DocumentData(name, surname, pesel)

    @staticmethod
    def get_license_data():
        document_data = RequestManager.get_document_data()
        category = input('Category: ')
        return LicenseData(document_data, category)

    @staticmethod
    def get_address():
        city = input('City: ')
        street = input('Street: ')
        building = input('Building: ')
        return Address(city, street, building)

    @staticmethod
    def get_residence_data():
        name = input('Name: ')
        surname = input('Surname: ')
        return ResidentRegistrationData(name, surname, RequestManager.get_address())

    @staticmethod
    def get_request_id():
        request_id = input('Your request id: ')
        return int(request_id)

    @staticmethod
    def get_request_type():
        request_type_str = None
        while request_type_str not in TYPES.keys():
            request_type_str = input(f'Enter your request type (possible {list(TYPES.keys())}): ')

        return TYPES[request_type_str]
