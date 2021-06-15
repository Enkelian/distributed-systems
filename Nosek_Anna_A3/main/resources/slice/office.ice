
#ifndef CALC_ICE
#define CALC_ICE

module TheOffice
{
  enum RequestType { ID, LICENSE, PASSPORT, RESIDENTREGISTRATION };

  class Address{
    string city;
    string street;
    string number;
  };

  struct Date{
      int year;
      int month;
      int day;
  };
  struct Time{
    int hour;
    int minute;
    int second;
  };

  struct DateTime{
    Date date;
    Time time;
  };

  class Data{

  };

  class ResidentRegistrationData extends Data{
    string name;
    string surname;
    Address address;
  };

  class DocumentData extends Data {
    string name;
    string surname;
    string pesel;
  };

  class LicenseData extends Data {
    DocumentData documentData;
    string category;
  };

  class Response {
    string message;
  };

  class ErrorResponse extends Response {
    string errorDetails;
  };

  class DocumentResponse extends Response {
    DateTime collectionDate;
  };

  class ResidentRegistrationResponse extends Response{
    Address address;
  };

  struct Request{
    RequestType type;
    Data data;
  };

  struct OfficeDetails{
    long requestID;
    DateTime expectedResponseDateTime;
  };

  struct RequestData{
    Request request;
    OfficeDetails officeData;
  };

  interface Office{
    OfficeDetails sendRequest(Request request);
    Response getResult(long requestID);
  };


};

#endif
