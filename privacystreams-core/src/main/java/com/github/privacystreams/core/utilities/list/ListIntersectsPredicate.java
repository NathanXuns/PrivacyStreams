package com.github.privacystreams.core.utilities.list;

import java.util.List;

/**
 * Created by yuanchun on 28/11/2016.
 * a predicate that makes list-related comparisons on field values
 */
final class ListIntersectsPredicate extends ListProcessor<Boolean> {

    private final List<Object> listToCompare;

    ListIntersectsPredicate(final String listField, final List<Object> listToCompare) {
        super(listField);
        this.listToCompare = listToCompare;
    }

    @Override
    protected Boolean processList(List<Object> list) {
        for (Object element : list) {
            if (listToCompare.contains(element))
                return true;
        }
        return false;
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();
        parameters.add(listToCompare);
        return parameters;
    }

}