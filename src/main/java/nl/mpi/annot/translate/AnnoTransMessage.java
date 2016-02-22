/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.util.ArrayList;

/**
 *
 * @author petbei
 */
public class AnnoTransMessage {
    static enum AnnoTransMessageSeverity {
        INFO,       // no irregularities encountered; no consequences for program execution
        WARNING,    // possible irregularities encountered; not necessarily problematic. Program can continue execution.
        ERROR,      // problematic irregularities encountered. Program can continue, but output will be incomplete or wrong.
        FATAL       // problematic irregularities encountered. Prgram execution can not continue in a meaningful way, and should be halted.
    };
    
    public static final String[] AnnoTransMessageSeverityText = {
        "INFO",     // no irregularities encountered; no consequences for program execution
        "WARNING",  // possible irregularities encountered; not necessarily problematic. Program can continue execution.
        "ERROR",    // problematic irregularities encountered. Program can continue, but output will be incomplete or wrong.
        "FATAL"     // problematic irregularities encountered. Prgram execution can not continue in a meaningful way, and should be halted.        
    };
    
    static enum AnnoTransMessageCode {
        AT_OK,                              // normal successful completion
        AT_FAIL_XMLsourceNotFound,          // required XML uri not found
        AT_FAIL_XMLparseError,              // problems in processing XML input
        AT_FAIL_XMLincomplete,              // XML parses but required elements are not specified
        AT_FAIL_noInput,                    // input String to be translated is null
        AT_FAIL_emptyImput,                 // input String to be translated is empty
        AT_FAIL_undefTransTable,            // requested translation table is not defined
        AT_FAIL_undefAnnotationLayer,       // requested annotation layer is not defined
        AT_FAIL_undefFromFormat,            // annotation format for input (source) String is not defined
        AT_FAIL_undefToFormat,              // annotation format for output (target, destination) String is not defined
        AT_FAIL_untranslatableInput,        // input String as a whole can not be translated
        AT_FAIL_untranslatableInputParts,   // input String contains parts that can not be translated
        AT_FAIL_wasInErrorState             // action can't be performed as a result of a previous error
    };
    
    public static final String[] AnnoTransMessageCodeText = {
        "Annotation Translation OK",                                    // normal successful completion
        "FAILURE: required XML input resource can not be found",        // required XML uri not found
        "FAILURE: XML input resource can not be processed",             // problems in processing XML input
        "FAILURE: XML input resource is incomplete",                    // XML parses but required elements are not specified
        "FAILURE: invalid (null) iput string",                          // input String to be translated is null
        "FAILURE: invalid (empty) iput string",                         // input String to be translated is empty
        "FAILURE: non-existent translation table",                      // requested translation table is not defined
        "FAILURE: non-existent annotation layer",                       // requested annotation layer is not defined
        "FAILURE: non-existent input format",                           // annotation format for input (source) String is not defined
        "FAILURE: non-existent output format",                          // annotation format for output (target, destination) String is not defined
        "FAILURE: no full-string translation possible",                 // input String as a whole can not be translated
        "FAILURE: string parts can not be translated",                  // input String contains parts that can not be translated
        "FAILURE: no translation possible because of previous errors"   // action can't be performed as a result of previous problems
    };
    
    private static ArrayList<AnnoTransMessage> messages;
    private AnnoTransMessageSeverity severity;
    private AnnoTransMessageCode code;
    private String text;
    
    public AnnoTransMessage(AnnoTransMessageSeverity s, AnnoTransMessageCode c, String t) {
        severity = s;
        code = c;
        text = t;
        messages.add(this);
    }

    public String getMessageText() {
        return ("*** " + AnnoTransMessageSeverityText[severity.ordinal()] + " *** [" + AnnoTransMessageCodeText[code.ordinal()] + "] " + text);
    }
    
    public void setSeverity(AnnoTransMessageSeverity s) {
        severity = s;
    }
    
    public void setCode(AnnoTransMessageCode c) {
        code = c;
    }
    
    public void setText(String t) {
        text = t;
    }
    
    public AnnoTransMessageSeverity getSeverity() {
        return severity;
    }
    
    public String getSeverityText() {
        return AnnoTransMessageSeverityText[severity.ordinal()];
    }
    
    public AnnoTransMessageCode getCode() {
        return code;
    }
    
    public String getCodeText() {
        return AnnoTransMessageCodeText[code.ordinal()];
    }
    
    public String getText() {
        return text;
    }
}
