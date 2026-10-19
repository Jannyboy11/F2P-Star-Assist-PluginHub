package com.janboerman.f2pstarassist.common.util;

import java.util.*;

public class CollectionConvert {

    private CollectionConvert() {
    }

    public static <E> Set<E> toSet(Collection<E> coll) {
        if (coll instanceof Set) {
            return (Set<E>) coll;
        } else {
            return new LinkedHashSet<>(coll);
        }
    }

}
