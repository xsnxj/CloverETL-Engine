/* Generated By:JJTree: Do not edit this line. CLVFMinusMinusNode.java */

package org.jetel.interpreter.ASTnode;
import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserVisitor;
public class CLVFIncrDecrStatement extends SimpleNode {
  
	public int kind;
    
  public CLVFIncrDecrStatement(int id) {
    super(id);
  }

  public CLVFIncrDecrStatement(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public void setKind(int kind) {
      this.kind=kind;;
  }
  
}
