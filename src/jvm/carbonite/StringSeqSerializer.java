package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** User: sritchie Date: 1/21/12 Time: 8:19 PM */
public class StringSeqSerializer extends Serializer {
  final Var readStringSeq;
  final Var printStringSeq;

  public StringSeqSerializer() {
    JavaBridge.requireCarbonite();
    readStringSeq = RT.var("carbonite.serializer", "read-string-seq");
    printStringSeq = RT.var("carbonite.serializer", "write-string-seq");
  }

  public void write(Kryo kryo, Output output, Object o) {
    printStringSeq.invoke(output, o);
  }

  public Object read(Kryo kryo, Input input, Class aClass) {
    return readStringSeq.invoke(input);
  }
}
