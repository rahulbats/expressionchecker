import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

public class ExpressionTest {
    @Test
    public void testJsonPath(){
        ExpressionEvaluator ee = new ExpressionEvaluator();

        // The expression will have two "int" parameters: "a" and "b".
        ee.setParameters(new String[] { "$" }, new Class[] { JsonNode.class });

        // And the expression (i.e. "result") type is also "int".
        ee.setExpressionType(Boolean.class);
        ObjectMapper mapper = new ObjectMapper();
        // And now we "cook" (scan, parse, compile and load) the fabulous expression.
        try {
            ee.cook("$.get(\"test\").asText().equals(\"rahul\")");
            //ee.cook("$.get(\"test\")");
            // Eventually we evaluate the expression - and that goes super-fast.
            boolean result = (Boolean) ee.evaluate(new Object[] { mapper.readTree("{\"test\":\"rahul\"}") });
            //String result = (String) ee.evaluate(new Object[] { mapper.readTree("{\"test\":\"rahul\"}") });
            System.out.println(result);
        } catch (CompileException e) {
            e.printStackTrace();
        } catch (Parser.ParseException e) {
            e.printStackTrace();
        } catch (Scanner.ScanException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
