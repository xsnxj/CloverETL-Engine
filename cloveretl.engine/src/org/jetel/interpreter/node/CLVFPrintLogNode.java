/* Generated By:JJTree: Do not edit this line. CLVFPrintLogNode.java */

package org.jetel.interpreter.node;

import org.jetel.interpreter.TransformLangParser;
import org.jetel.interpreter.TransformLangParserVisitor;

public class CLVFPrintLogNode extends SimpleNode {
    
    public int level;
    
  public CLVFPrintLogNode(int id) {
    super(id);
  }

  public CLVFPrintLogNode(TransformLangParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public void setLevel(int level){
      this.level=level;
  }
  
}
