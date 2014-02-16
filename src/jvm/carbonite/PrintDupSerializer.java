package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** User: sritchie Date: 1/21/12 Time: 7:57 PM */
public class PrintDupSerializer extends Serializer {
  final Var cljRead;
  final Var cljPrintDup;

  public PrintDupSerializer() {
    JavaBridge.requireCarbonite();
    cljRead = RT.var("carbonite.serializer", "clj-read");
    cljPrintDup = RT.var("carbonite.serializer", "clj-print-dup");
  }

  public void write(Kryo kryo, Output output, Object o) {
    cljPrintDup.invoke(output, o);
  }

  public Object read(Kryo kryo, Input input, Class aClass) {
    return cljRead.invoke(input);
  }
}
