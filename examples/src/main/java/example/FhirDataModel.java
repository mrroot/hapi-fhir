package example;

import java.util.Date;
import java.util.List;
import java.util.Set;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class FhirDataModel {

   public static void datatypes() {
      // START SNIPPET: datatypes
      Observation obs = new Observation();

      // These are all equivalent
      obs.setIssued(new InstantDt(new Date()));
      obs.setIssued(new Date(), TemporalPrecisionEnum.MILLI);
      obs.setIssuedWithMillisPrecision(new Date());

      // The InstantDt also lets you work with the instant as a Java Date
      // object or as a FHIR String.
      Date date = obs.getIssuedElement().getValue(); // A date object
      String dateString = obs.getIssuedElement().getValueAsString(); // "2014-03-08T12:59:58.068-05:00"
      // END SNIPPET: datatypes

      System.out.println(date);
      System.out.println(dateString);

   }

   @SuppressWarnings("unused")
   public void nonNull() {
      // START SNIPPET: nonNull
      Observation observation = new Observation();

      // None of these calls will not return null, but instead create their
      // respective
      // child elements.
      List<IdentifierDt> identifierList = observation.getIdentifier();
      CodeableConceptDt code = observation.getCode();
      StringDt textElement = observation.getCode().getTextElement();

      // DateTimeDt is a FHIR primitive however, so the following will return
      // null
      // unless a value has been placed there.
      Date active = observation.addIdentifier().getPeriod().getStartElement().getValue();
      // END SNIPPET: nonNull

   }

   @SuppressWarnings("unused")
   public static void codes() {
      // START SNIPPET: codes
      Patient patient = new Patient();

      // You can set this code using a String if you want. Note that
      // for "closed" valuesets (such as the one used for Patient.gender)
      // you must use one of the strings defined by the FHIR specification.
      // You must not define your own.
      patient.getGenderElement().setValue("male");
      
      // HAPI also provides Java enumerated types which make it easier to
      // deal with coded values. This code achieves the exact same result
      // as the code above.
      patient.setGender(AdministrativeGenderEnum.MALE);
      
      // You can also retrieve coded values the same way
      String genderString = patient.getGenderElement().getValueAsString();
      AdministrativeGenderEnum genderEnum = patient.getGenderElement().getValueAsEnum();
      
      // The following is a shortcut to create
      patient.setMaritalStatus(MaritalStatusCodesEnum.M);
      // END SNIPPET: codes

   }

   
   @SuppressWarnings("unused")
   public static void codeableConcepts() {
      // START SNIPPET: codeableConcepts
      Patient patient = new Patient();

      // Coded types can naturally be set using plain strings
      CodingDt statusCoding = patient.getMaritalStatus().addCoding();
      statusCoding.setSystem("http://hl7.org/fhir/v3/MaritalStatus");
      statusCoding.setCode("M");
      statusCoding.setDisplay("Married");

      // You could add a second coding to the field if needed too. This
      // can be useful if you want to convey the concept using different
      // codesystems.
      CodingDt secondStatus = patient.getMaritalStatus().addCoding();
      secondStatus.setCode("H");
      secondStatus.setSystem("http://example.com#maritalStatus");
      secondStatus.setDisplay("Happily Married");
      
      // CodeableConcept also has a text field meant to convey 
      // a user readable version of the concepts it conveys.
      patient.getMaritalStatus().setText("Happily Married");
      
      // There are also accessors for retrieving values
      String firstCode  = patient.getMaritalStatus().getCoding().get(0).getCode();
      String secondCode = patient.getMaritalStatus().getCoding().get(1).getCode();
      // END SNIPPET: codeableConcepts

   }

   @SuppressWarnings("unused")
   public static void codeableConceptEnums() {
      // START SNIPPET: codeableConceptEnums
      Patient patient = new Patient();
      
      // Set the CodeableConcept's first coding to use the code
      // and codesystem associated with the M value.
      patient.setMaritalStatus(MaritalStatusCodesEnum.M);
      
      // If you need to set other fields (such as the display name) after
      // using the Enum type, you may still do so.
      patient.getMaritalStatus().getCodingFirstRep().setDisplay("Married");
      patient.getMaritalStatus().getCodingFirstRep().setPrimary(true);
      patient.getMaritalStatus().getCodingFirstRep().setVersion("1.0");
      
      // You can use accessors to retrieve values from CodeableConcept fields
      
      // Returns "M"
      String code = patient.getMaritalStatus().getCodingFirstRep().getCode();

      // Returns "http://hl7.org/fhir/v3/MaritalStatus". This value was also
      // populated via the enum above.
      String codeSystem = patient.getMaritalStatus().getCodingFirstRep().getCode();
      
      // In many cases, Enum types can be used to retrieve values as well. Note that
      // the setter takes a single type, but the getter returns a Set, because the
      // field can technicaly contain more than one code and codesystem. BE CAREFUL
      // when using this method however, as no Enum will be returned in the case
      // that the field contains only a code other than the ones defined by the Enum. 
      Set<MaritalStatusCodesEnum> status = patient.getMaritalStatus().getValueAsEnum();
      // END SNIPPET: codeableConceptEnums

   }

   
   public static void main(String[] args) {
      datatypes();

      // START SNIPPET: observation
      Observation observation = new Observation();
      
      // Create a quantity datatype
      QuantityDt q = new QuantityDt();
      q.setValue(185);
      q.setSystem("http://unitsofmeasure.org");
      q.setCode("lbs");
      
      // Put the datatype in the observation
      observation.setValue(q);
      
      // Set the reference range
      observation.getReferenceRangeFirstRep().setLow(new QuantityDt(100));
      observation.getReferenceRangeFirstRep().setHigh(new QuantityDt(200));
      
      // END SNIPPET: observation
      
      
   }

   public void namesHard() {
      // START SNIPPET: namesHard
      Patient patient = new Patient();
      HumanNameDt name = patient.addName();
      StringDt family = name.addFamily();
      family.setValue("Smith");
      StringDt firstName = name.addGiven();
      firstName.setValue("Rob");
      StringDt secondName = name.addGiven();
      secondName.setValue("Bruce");
      // END SNIPPET: namesHard
   }

   public void namesEasy() {
      // START SNIPPET: namesEasy
      Patient patient = new Patient();
      patient.addName().addFamily("Smith").addGiven("Rob").addGiven("Bruce");
      // END SNIPPET: namesEasy
   }

}
