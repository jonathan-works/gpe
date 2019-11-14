package br.com.infox.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Node;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.relational.Size;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;

import br.com.infox.epp.system.Configuration;

public class UUIDGenericType implements SingleColumnType<UUID> {
    
    public static final String TYPE_NAME = "uuid-generic";
    public static final SingleColumnType<UUID> INSTANCE = new UUIDGenericType();
    private static final long serialVersionUID = 1L;

    private SingleColumnType<UUID> realType;

    public UUIDGenericType() {
        realType = Configuration.getInstance().getDatabase().getUUIDType();
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public boolean isAssociationType() {
        return realType.isAssociationType();
    }

    @Override
    public boolean isCollectionType() {
        return realType.isCollectionType();
    }

    @Override
    public boolean isEntityType() {
        return realType.isEntityType();
    }

    @Override
    public boolean isAnyType() {
        return realType.isAnyType();
    }

    @Override
    public boolean isComponentType() {
        return realType.isComponentType();
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return realType.getColumnSpan(mapping);
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return realType.sqlTypes(mapping);
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return realType.dictatedSizes(mapping);
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return realType.dictatedSizes(mapping);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getReturnedClass() {
        return realType.getReturnedClass();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isXMLElement() {
        return realType.isXMLElement();
    }

    @Override
    public boolean isSame(Object x, Object y) throws HibernateException {
        return realType.isSame(x, y);
    }

    @Override
    public boolean isEqual(Object x, Object y) throws HibernateException {
        return realType.isEqual(x, y);
    }

    @Override
    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
            throws HibernateException {
        return realType.isEqual(x, y, factory);
    }

    @Override
    public int getHashCode(Object x) throws HibernateException {
        return realType.getHashCode(x);
    }

    @Override
    public int getHashCode(Object x, SessionFactoryImplementor factory)
            throws HibernateException {
        return realType.getHashCode(x, factory);
    }

    @Override
    public int compare(Object x, Object y) {
        return realType.compare(x, y);
    }

    @Override
    public boolean isDirty(Object old, Object current,
            SessionImplementor session) throws HibernateException {
        return realType.isDirty(old, current, session);
    }

    @Override
    public boolean isDirty(Object oldState, Object currentState,
            boolean[] checkable, SessionImplementor session)
            throws HibernateException {
        return realType.isDirty(oldState, currentState, checkable, session);
    }

    @Override
    public boolean isModified(Object dbState, Object currentState,
            boolean[] checkable, SessionImplementor session)
            throws HibernateException {
        return realType.isModified(dbState, currentState, checkable, session);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return realType.nullSafeGet(rs, names, session, owner);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String name,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return realType.nullSafeGet(rs, name, session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            boolean[] settable, SessionImplementor session)
            throws HibernateException, SQLException {
        realType.nullSafeSet(st, value, index, settable, session);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
        realType.nullSafeSet(st, value, index, session);
    }

    @Override
    public String toLoggableString(Object value,
            SessionFactoryImplementor factory) throws HibernateException {
        return realType.toLoggableString(value, factory);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setToXMLNode(Node node, Object value,
            SessionFactoryImplementor factory) throws HibernateException {
        realType.setToXMLNode(node, value, factory);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object fromXMLNode(Node xml, Mapping factory)
            throws HibernateException {
        return realType.fromXMLNode(xml, factory);
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory)
            throws HibernateException {
        return realType.deepCopy(value, factory);
    }

    @Override
    public boolean isMutable() {
        return realType.isMutable();
    }

    @Override
    public Serializable disassemble(Object value, SessionImplementor session,
            Object owner) throws HibernateException {
        return realType.disassemble(value, session, owner);
    }

    @Override
    public Object assemble(Serializable cached, SessionImplementor session,
            Object owner) throws HibernateException {
        return realType.assemble(cached, session, owner);
    }

    @Override
    public void beforeAssemble(Serializable cached, SessionImplementor session) {
        realType.beforeAssemble(cached, session);
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return realType.hydrate(rs, names, session, owner);
    }

    @Override
    public Object resolve(Object value, SessionImplementor session, Object owner)
            throws HibernateException {
        return realType.resolve(value, session, owner);
    }

    @Override
    public Object semiResolve(Object value, SessionImplementor session,
            Object owner) throws HibernateException {
        return realType.semiResolve(value, session, owner);
    }

    @Override
    public Type getSemiResolvedType(SessionFactoryImplementor factory) {
        return realType.getSemiResolvedType(factory);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object replace(Object original, Object target,
            SessionImplementor session, Object owner, Map copyCache)
            throws HibernateException {
        return realType.replace(original, target, session, owner, copyCache);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object replace(Object original, Object target,
            SessionImplementor session, Object owner, Map copyCache,
            ForeignKeyDirection foreignKeyDirection) throws HibernateException {
        return realType.replace(original, target, session, owner, copyCache);
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return realType.toColumnNullness(value, mapping);
    }

    @Override
    public int sqlType() {
        return realType.sqlType();
    }

    @Override
    public String toString(UUID value) throws HibernateException {
        return realType.toString(value);
    }

    @Override
    public UUID fromStringValue(String xml) throws HibernateException {
        return realType.fromStringValue(xml);
    }

    @Override
    public UUID nullSafeGet(ResultSet rs, String name,
            SessionImplementor session) throws HibernateException, SQLException {
        return realType.nullSafeGet(rs, name, session);
    }

    @Override
    public Object get(ResultSet rs, String name, SessionImplementor session)
            throws HibernateException, SQLException {
        return realType.get(rs, name, session);
    }

    @Override
    public void set(PreparedStatement st, UUID value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
        realType.set(st, value, index, session);
    }
}
