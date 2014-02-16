package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** User: sritchie Date: 1/20/12 Time: 3:57 PM
 *
 * Define a serializer that utilizes the Clojure pr-str and
 * read-string functions to serialize/deserialize instances relying
 * solely on the printer/reader.  Probably not the most efficient but
 * likely to work in many cases.
 *
 */
public class ClojureReaderSerializer extends Serializer {
  final Var cljRead;
  final Var cljPrint;

  public ClojureReaderSerializer() {
    JavaBridge.requireCarbonite();
    cljRead = RT.var("carbonite.serializer", "clj-read");
    cljPrint = RT.var("carbonite.serializer", "clj-print");
  }

  public void write(Kryo kryo, Output output, Object o) {
    cljPrint.invoke(output, o);
  }

  public Object read(Kryo kryo, Input input, Class aClass) {
    return cljRead.invoke(input);
  }
}
