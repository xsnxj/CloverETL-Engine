/* Generated By:JJTree: Do not edit this line. CLVFLogNode.java */

package org.jetel.interpreter.node;
import org.jetel.interpreter.TransformLangParser;
import org.jetel.interpreter.TransformLangParserVisitor;

public class CLVFLogNode extends SimpleNode {
  public CLVFLogNode(int id) {
    super(id);
  }

  public CLVFLogNode(TransformLangParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
