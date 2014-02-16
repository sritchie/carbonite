package carbonite;

import clojure.lang.Ratio;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import java.math.BigInteger;

/** User: sritchie Date: 1/20/12 Time: 3:49 PM */
public class RatioSerializer extends Serializer<Ratio> {
  final DefaultSerializers.BigIntegerSerializer big  = new DefaultSerializers.BigIntegerSerializer();


  public void write(Kryo k, Output output, Ratio ratio) {
    big.write(k, output, ratio.numerator);
    big.write(k, output, ratio.denominator);
  }

  public Ratio read(Kryo kryo, Input input, Class<Ratio> ratioClass) {
    BigInteger num = big.read(kryo, input, null);
    BigInteger denom = big.read(kryo, input, null);

    return new Ratio(num, denom);
  }
}
