package com.github.privacystreams;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchun on 11/03/2017.
 */

public class PSOperatorWrapperDoc {

    ClassDoc classDoc;
    String name;
    String description;

    List<PSOperatorDoc> operatorDocs;

    private PSOperatorWrapperDoc(ClassDoc classDoc) {
        this.classDoc = classDoc;
        this.name = classDoc.name();
        this.description = classDoc.commentText();
        this.operatorDocs = new ArrayList<>();

        for (MethodDoc methodDoc : classDoc.methods()) {
            PSOperatorDoc operatorDoc = PSOperatorDoc.build(classDoc, methodDoc);
            if (operatorDoc != null) this.operatorDocs.add(operatorDoc);
        }
    }

    public static PSOperatorWrapperDoc build(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc annotationType = annotation.annotationType();
            if (Consts.OPERATOR_WRAPPER_ANNOTATION.equals(annotationType.toString())) {
                return new PSOperatorWrapperDoc(classDoc);
            }
        }
        return null;
    }

    public String toString() {
        String operatorWrapperDocStr = "";
        operatorWrapperDocStr += "## " + this.name + "\n";
        operatorWrapperDocStr += "Package: `" + this.classDoc.containingPackage() + "`\n\n";
        operatorWrapperDocStr += this.description + "\n";

        operatorWrapperDocStr += "### Operators\n";
        operatorWrapperDocStr += "| Input-->Output | Reference & Description |\n";
        operatorWrapperDocStr += "|----|----|\n";

        for (PSOperatorDoc operatorDoc : this.operatorDocs) {
            operatorWrapperDocStr += operatorDoc.toString() + "\n";
        }

        return operatorWrapperDocStr;
    }
}