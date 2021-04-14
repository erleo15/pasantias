package amef.processor;

import java.util.regex.Pattern;

public interface FileExtractor {

    String MICRODATA = "Microdata";
    String RDFa = "RDFa";
    String JSON = "JSON-LD";

    String ITEMSCOPE = "itemscope";
    String ITEMTYPE = "itemtype";
    String ITEMPROP = "itemprop";

    String VOCAB = "vocab";
    String TYPEOF = "typeof";
    String PROPERTY = "property";

    String SCRIPT = "script";
    String SCRIPTTYPE = "type";
    String JSONTYPE = "ld+json";
    String CONTEXT = "@context";
    String TYPE = "@type";
    String GRAPH = "@graph";


    String CONTENT = "content";

    String NONE = "none";

    Pattern SCHEMAURL = Pattern.compile("https?://schema\\.org.*");

    String HEAD = "head";
    String BODY = "body";
    String HTML = "html";
}
