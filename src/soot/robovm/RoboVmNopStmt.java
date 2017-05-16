package soot.robovm;

import soot.ValueBox;
import soot.jimple.internal.JNopStmt;

import java.util.Collections;
import java.util.List;

/**
 * @author Demyan Kimitsa
 * Special class that keep local variable references as used.
 * It is needed for case when there is local variable attached to simple goto statement
 */
public class RoboVmNopStmt extends JNopStmt {
    private final List<ValueBox> useBoxes;
    public RoboVmNopStmt(List<ValueBox> useBoxes) {
        this.useBoxes = useBoxes;
    }

    @Override
    public List getUseBoxes() {
        return Collections.unmodifiableList(useBoxes);
    }
}
