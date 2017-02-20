package com.github.privacystreams.core.utilities.string;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.github.privacystreams.core.utils.Logging;
import com.github.privacystreams.core.utils.HashUtils;

/**
 * Created by yuanchun on 30/12/2016.
 * A function that hash a given string and return the hashed string
 */
final class StringHashFunction extends StringProcessor<String> {

    private final String hashAlgorithm;

    StringHashFunction(String stringField, String hashAlgorithm) {
        super(stringField);
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    protected String processString(String stringValue) {
        try {
            return HashUtils.hash(stringValue, hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            Logging.warn("Hash function failed. Algorithm " + hashAlgorithm);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();
        parameters.add(this.hashAlgorithm);
        return parameters;
    }
}