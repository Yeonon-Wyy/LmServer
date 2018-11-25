package top.yeonon.lmserver.databind.param.strategy;

import top.yeonon.lmserver.http.LmRequest;

/**
 *
 * @Author yeonon
 * @date 2018/9/30 0030 17:40
 **/
public enum TypeNameEnum {
    STRING("Ljava/lang/String;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getStringParam(paramName);
        }
    },
    INTEGER("Ljava/lang/Integer;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getIntegerParam(paramName);
        }
    },
    LONG("Ljava/lang/Long;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getLongParam(paramName);
        }
    },
    BOOLEAN("Ljava/lang/Boolean;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getBooleanParam(paramName);
        }
    },
    FLOAT("Ljava/lang/Float;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getFloatParam(paramName);
        }
    },
    DOUBLE("Ljava/lang/Double;") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            return request.getDoubleParam(paramName);
        }
    },
    PRIMITIVE_INT("I") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Integer val = request.getIntegerParam(paramName);
            if (val == null)
                return 0;
            return val;
        }
    },
    PRIMITIVE_LONG("L") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Long val = request.getLongParam(paramName);
            if (val == null)
                return 0L;
            return val;
        }
    },
    PRIMITIVE_FLOAT("F") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Float val = request.getFloatParam(paramName);
            if (val == null)
                return 0.0f;
            return val;
        }
    },
    PRIMITIVE_DOUBLE("D") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Double val = request.getDoubleParam(paramName);
            if (val == null)
                return 0.0d;
            return val;
        }
    },
    PRIMITIVE_BOOLEAN("Z") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Boolean val = request.getBooleanParam(paramName);
            if (val == null)
                return false;
            return val;
        }
    },
    PRIMITIVE_BYTE("B") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Byte val = request.getByteParam(paramName);
            if (val == null)
                return 0;
            return val;
        }
    },
    PRIMITIVE_SHORT("S") {
        @Override
        public Object handle(String paramName, LmRequest request) {
            Short val = request.getShortParam(paramName);
            if (val == null)
                return 0;
            return val;
        }
    };


    private String description;

    TypeNameEnum(String description) {
        this.description = description;
    }

    public static TypeNameEnum getType(String name) {
        TypeNameEnum[] typeNameEnums = TypeNameEnum.values();
        for (TypeNameEnum typeNameEnum : typeNameEnums) {
            if (typeNameEnum.getDescription().equalsIgnoreCase(name)) {
                return typeNameEnum;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public abstract Object handle(String paramName, LmRequest request);
}
