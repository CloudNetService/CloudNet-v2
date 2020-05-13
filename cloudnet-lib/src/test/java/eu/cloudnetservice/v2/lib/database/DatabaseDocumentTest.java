package eu.cloudnetservice.v2.lib.database;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.v2.lib.serverselectors.sign.Position;
import eu.cloudnetservice.v2.lib.serverselectors.sign.Sign;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseDocumentTest {

    @Test
    public void saveAsConfig() {
    }

    @Test
    public void append() {
        DatabaseDocument document = new DatabaseDocument();
        String toAppend = "String to append";
        document.append("test", toAppend);
        Assert.assertEquals("Appended object does not equal gotten object",
                            toAppend,
                            document.getString("test")
        );
    }

    @Test
    public void getObject() {
        DatabaseDocument document = new DatabaseDocument();
        List<Float> toAppend = new ArrayList<>(Collections.singletonList(0.5f));
        document.append("test", toAppend);
        final Type listOfFloats = TypeToken.getParameterized(List.class, Float.class).getType();
        Assert.assertEquals("Appended object does not equal gotten object",
                            toAppend,
                            document.getObject("test", listOfFloats)
        );

    }

    @Test
    public void contains() {
        DatabaseDocument document = new DatabaseDocument();
        List<Float> toAppend = Collections.singletonList(0.5f);
        document.append("test", toAppend);
        Assert.assertTrue("Document does not contain appended object",
                          document.contains("test")
        );
    }

    @Test
    public void getString() {
        DatabaseDocument document = new DatabaseDocument();
        document.append("test", "Test");
        Assert.assertEquals("Appended string does not equal gotten string",
                            "Test",
                            document.getString("test")
        );
    }

    @Test
    public void getLong() {
        DatabaseDocument document = new DatabaseDocument();
        document.append("test", 1L);
        Assert.assertEquals("Appended long does not equal gotten long",
                            1L,
                            document.getLong("test")
        );
    }

    @Test
    public void getInt() {
        DatabaseDocument document = new DatabaseDocument();
        document.append("test", 1);
        Assert.assertEquals("Appended long does not equal gotten long",
                            1,
                            document.getInt("test")
        );
    }

    @Test
    public void toDocument() {
        DatabaseDocument document = new DatabaseDocument();
        document.append("test", "Test");
        System.out.println(document.toDocument());
        Assert.assertEquals("String in Document differs from string in DatabaseDocument",
                            document.toDocument().getString("test"),
                            "Test");
    }

    @Test
    public void constructorDocument() {
        Document document = new Document("CloudNet Document");
        document.append("primitive", 1);
        document.append("array", Collections.singletonList(2));
        document.append("object", new Sign("test",
                                           new Position("test", "world", 0.0D, 0.0D, 0.0D)));

        //noinspection MismatchedQueryAndUpdateOfCollection
        DatabaseDocument databaseDocument = new DatabaseDocument(document);
        Assert.assertEquals("Primitive is not equal",
                            (int) document.getObject("primitive", Integer.class),
                            databaseDocument.getInt("primitive"));
        final Type INT_LIST_TYPE = TypeToken.getParameterized(List.class, Integer.class).getType();

        Assert.assertEquals("Array is not equal",
                            document.<List<Integer>>getObject("array", INT_LIST_TYPE),
                            databaseDocument.<List<Integer>>getObject("array", INT_LIST_TYPE));
        Assert.assertEquals("Object is not equal",
                            document.<Sign>getObject("object", Sign.TYPE),
                            databaseDocument.<Sign>getObject("object", Sign.TYPE));

    }
}
