package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import soot.SootClass;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.options.Options;
import soot.tagkit.SourceFileTag;

public class JVirtualInvokeExpr extends AbstractVirtualInvokeExpr {

  public JVirtualInvokeExpr(Value base, SootMethodRef methodRef, List<? extends Value> args) {
    super(Jimple.v().newLocalBox(base), methodRef, new ValueBox[args.size()]);

    if (!Options.v().ignore_resolution_errors()) {
      final SootClass sc = methodRef.declaringClass();
      // Check that the method's class is resolved enough
      sc.checkLevelIgnoreResolving(SootClass.HIERARCHY);
      // now check if the class is valid
      if (sc.isInterface()) {
        SourceFileTag tag = (SourceFileTag) sc.getTag(SourceFileTag.NAME);
        throw new RuntimeException("Trying to create virtual invoke expression for interface type ("
                + sc.getPathPlusClassName() + " in file " + (tag != null ? tag.getAbsolutePath() : "unknown")
                + "). Use JInterfaceInvokeExpr instead!");
      }
    }

    final Jimple jimp = Jimple.v();
    for (ListIterator<? extends Value> it = args.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.argBoxes[it.previousIndex()] = jimp.newImmediateBox(v);
    }
  }

  @Override
  public Object clone() {
    final int count = getArgCount();
    List<Value> clonedArgs = new ArrayList<Value>(count);
    for (int i = 0; i < count; i++) {
      clonedArgs.add(Jimple.cloneIfNecessary(getArg(i)));
    }
    return new JVirtualInvokeExpr(getBase(), methodRef, clonedArgs);
  }
}
