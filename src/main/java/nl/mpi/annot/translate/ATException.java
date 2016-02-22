/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.annot.translate;

import java.lang.Exception;

/**
 *
 * @author petbei
 */
public class ATException extends Exception {
    ATException(String msg) {
        super(msg);
    }
    ATException(String msg, Throwable thr) {
        super(msg, thr);
    }
}
