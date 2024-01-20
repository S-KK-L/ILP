package uk.ac.ed.inf.Utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;

import java.io.IOException;

public class CreditCardInformationDeserializer extends JsonDeserializer<CreditCardInformation> {
    // Override the deserialize method to convert JSON data into a CreditCardInformation object
    @Override
    public CreditCardInformation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException
    {
        // Parse the JSON using the JsonParser and obtain a JsonNode
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String creditCardNumber = node.get("creditCardNumber").asText();
        String creditCardExpiry = node.get("creditCardExpiry").asText();
        String cvv = node.get("cvv").asText();

        // Return a new CreditCardInformation object created with the extracted data
        return new CreditCardInformation(creditCardNumber, creditCardExpiry, cvv);
    }
}