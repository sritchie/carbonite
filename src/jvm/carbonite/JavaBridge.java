package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;

public class JavaBridge {
    static final Var require = RT.var("clojure.core", "require");
    static final Var symbol = RT.var("clojure.core", "symbol");

    static Var defaultReg;
    static Var regSerializers;
    static Var cljPrimitives;
    static Var javaPrimitives;
    static Var cljCollections;
    
    static boolean initialized = false;

    public static synchronized void initialize() {
        if(!initialized) {
            try {
                require.invoke(symbol.invoke("carbonite.api"));
                requireCarbonite();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            defaultReg = RT.var("carbonite.api", "default-registry");
            regSerializers = RT.var("carbonite.api", "register-serializers");
            cljPrimitives  = RT.var("carbonite.serializer", "clojure-primitives");
            javaPrimitives = RT.var("carbonite.serializer", "java-primitives");
            cljCollections = RT.var("carbonite.serializer", "clojure-collections");

            initialized = true;
        }
    }

    public static synchronized void registerPrimitives(Kryo registry) throws Exception {
        initialize();
        regSerializers.invoke(registry, cljPrimitives.deref());
    }

    public static synchronized void registerCollections(Kryo registry) throws Exception {
        initialize();
        regSerializers.invoke(registry, cljCollections.deref());
    }

    public static synchronized void registerJavaPrimitives(Kryo registry) throws Exception {
        initialize();
        regSerializers.invoke(registry, javaPrimitives.deref());
    }

    public static synchronized Kryo defaultRegistry() throws Exception {
        initialize();
        return (Kryo) defaultReg.invoke();
    }

    public static synchronized void requireCarbonite () {
        require.invoke(symbol.invoke("carbonite.serializer"));
    }

    public static synchronized void enhanceRegistry(Kryo registry) throws Exception {
        registerPrimitives(registry);
        registerJavaPrimitives(registry);
        registerCollections(registry);
    }
}
