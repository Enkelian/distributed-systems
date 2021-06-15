package sr.serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import sr.proto.AddressBookProtos.Person;

public class ProtoSerialization {

	public static void main(String[] args)
	{
		try {
			new ProtoSerialization().testProto();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testProto() throws IOException
	{
		Person p1 =
				  Person.newBuilder()
				    .setId(123456)
				    .setName("W?odzimierz Wr?blewski")
				    .setEmail("wrobel@poczta.com")
				    .addPhones(
						      Person.PhoneNumber.newBuilder()
						        .setNumber("+48-12-555-4321")
						        .setType(Person.PhoneType.HOME))
				    .addPhones(
						      Person.PhoneNumber.newBuilder()
						        .setNumber("+48-699-989-796")
						        .setType(Person.PhoneType.MOBILE))
//				    .setIncomes(
//				    		Person.Incomes.newBuilder()
//								.addAllMonthlyValue(List.of(20.0, 30.0, 40.0, 1.9))
//								.build()
//					)
				    .build();
		
		byte[] p1ser = null;
		long startTime = System.nanoTime();

		long n = 1000000;
        System.out.println("Performing proto serialization " + n + " times...");
        for(long i = 0; i < n; i++)
		{
			p1ser = p1.toByteArray();
		}
        System.out.println("... finished.");
        
        //serialize again (only once) and write to a file
		FileOutputStream file = new FileOutputStream("person2.ser"); 
		file.write(p1.toByteArray());
		System.out.println("Size=" + p1.toByteArray().length);
		System.out.println(Arrays.toString(p1.toByteArray()));
		System.out.println(p1.toByteArray()[2]);
		System.out.println(p1.toByteArray()[32]);

		file.close();
		System.out.println((System.nanoTime() - startTime)/n);

	}	
}
