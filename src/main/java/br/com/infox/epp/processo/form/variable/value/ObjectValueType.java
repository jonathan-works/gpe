package br.com.infox.epp.processo.form.variable.value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.hibernate.util.HibernateUtil;

public abstract class ObjectValueType implements ValueType {
    
    protected String name;
    
    public ObjectValueType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public static class JPAValueType extends ObjectValueType {

        public JPAValueType() {
            super("jpa");
        }

        @Override
        public Object convertToModelValue(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                Gson gson = new GsonBuilder().create();
                JsonObject jsonObject = gson.fromJson((String) value, JsonObject.class);
                try {
                    Class<?> entityClass = Class.forName(jsonObject.get("entityClass").getAsString());
                    Number id = jsonObject.get("id").getAsNumber();
                    Object entityObject = EntityManagerProducer.getEntityManager().find(entityClass, id);
                    return entityObject;
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot convert " + value + " to jpa value type", e);
                }
            }
            return value;
        }

        @Override
        public String convertToStringValue(Object value) {
            if (value == null) return null;
            Class<?> entityClass = HibernateUtil.removeProxy(value).getClass();
            Number id = (Number) EntityManagerProducer.instance().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(value);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("entityClass", entityClass.getName());
            jsonObject.addProperty("id", id);
            return jsonObject.toString();
        }
    }
    
    public static class JSONValueType extends ObjectValueType {

        public JSONValueType() {
            super("json");
        }

        @Override
        public Object convertToModelValue(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                Gson gson = new GsonBuilder().create();
                JsonObject jsonObject = gson.fromJson((String) value, JsonObject.class);
                try {
                    Class<?> clazz = Class.forName(jsonObject.get("clazz").getAsString());
                    Object object = gson.fromJson(jsonObject.get("json").getAsString(), clazz);
                    return object;
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Cannot convert " + value + " to json value type", e);
                }
            }
            return value;
        }

        @Override
        public String convertToStringValue(Object value) {
            Gson gson = new GsonBuilder().create();
            Class<?> clazz = value.getClass();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("objectClass", clazz.getName());
            jsonObject.addProperty("json", gson.toJson(value));
            return jsonObject.toString();
        }
    }
}
