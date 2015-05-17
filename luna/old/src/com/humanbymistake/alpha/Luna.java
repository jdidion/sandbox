package com.humanbymistake.alpha;

import com.humanbymistake.alpha.data.Object;

public class Luna {
    /**
     * Evaluate <var>obj</var> and return its value. If <var>obj</var> is a method, eval
     * returns the return value of the method. If <var>obj</var> is a primitive type,
     * eval returns <var>obj</var>. Otherwise eval returns <var>obj</var>.getName().
     * @param obj
     * @return
     */
    public static Object eval(Object obj) {
        if (isMethod(obj)) {

        } else if (isPrimitive(obj)) {

        } else {
            return obj.getName();
        }
    }

    /**
     * Used for "casted" evals - evaluating a member of an ancestor of an
     * object within the context of that object. So, given the following
     * object heirarchy: Obj1 -> Obj2, and that Obj1 has a method M1 that
     * is overridden by Obj2, eval(Obj2.getParent("Obj1").getMember("M1"), Obj2)
     * evaluates the M1 method defined by Obj1 in the context of Obj2 (i.e. using
     * any of Obj2's members that overide Obj1's members. The only exception is
     * if Obj1.M1 is re-entrant. In this case Obj1's M1 will be used.
     * @param obj
     * @param context
     * @return
     */
    public static Object eval(Object obj, Object context) {

    }
}
