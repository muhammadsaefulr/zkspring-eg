/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfincoreplus.db;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import org.apache.ibatis.logging.Log;

/**
 *
 * @author user
 */
public class NamedParameterStatement {

/**
     * The statement this object is wrapping. 
     */ 
    private final PreparedStatement statement; 
 
    /**
     * Maps parameter names to arrays of ints which are the parameter indices. 
     */ 
    private final Map<String, int[]> indexMap; 
 
    private final String query; 
    private final Integer options; 
 
 
    /**
     * Creates a NamedParameterStatement.  Wraps a call to 
     * c.{@link Connection#prepareStatement(String) prepareStatement}.
     * 
     * @param connection the database connection 
     * @param query      the parameterized query 
     * @throws SQLException if the statement could not be created 
     */ 
    public NamedParameterStatement(Connection connection, String query) throws SQLException { 
        indexMap = new HashMap<>(); 
        String parsedQuery = parse(query, indexMap); 
        statement = connection.prepareStatement(parsedQuery); 
        this.query = query; 
        this.options = null; 
    } 
 
    public NamedParameterStatement(Connection connection, String query, int options) throws SQLException { 
        indexMap = new HashMap<>(); 
        String parsedQuery = parse(query, indexMap); 
        statement = connection.prepareStatement(parsedQuery, options); 
        this.query = query; 
        this.options = options; 
    } 
 
    public String getQuery() { 
        return query; 
    } 
 
    public Integer getOptions() { 
        return options; 
    } 
 
    /**
     * Parses a query with named parameters.  The parameter-index mappings are 
     * put into the map, and the 
     * parsed query is returned.  DO NOT CALL FROM CLIENT CODE.  This 
     * method is non-private so JUnit code can 
     * test it. 
     * 
     * @param query    query to parse 
     * @param paramMap map to hold parameter-index mappings 
     * @return the parsed query 
     */ 
    static final String parse(String query, Map<String, int[]> paramMap) { 
        // I was originally using regular expressions, but they didn't work well 
        // for ignoring parameter-like strings inside quotes. 
        Map<String, List<Integer>> paramMapAux = new HashMap<>(); 
        int length = query.length(); 
        StringBuilder parsedQuery = new StringBuilder(length); 
        boolean inSingleQuote = false; 
        boolean inDoubleQuote = false; 
        int index = 1; 
 
        for (int i = 0; i < length; i++) { 
            char c = query.charAt(i); 
            if (inSingleQuote) { 
                if (c == '\'') { 
                    inSingleQuote = false; 
                } 
            } else if (inDoubleQuote) { 
                if (c == '"') { 
                    inDoubleQuote = false; 
                } 
            } else { 
                if (c == '\'') { 
                    inSingleQuote = true; 
                } else if (c == '"') { 
                    inDoubleQuote = true; 
                } else if (c == ':' && i + 1 < length && 
                        Character.isJavaIdentifierStart(query.charAt(i + 1))) { 
                    int j = i + 2; 
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) { 
                        j++; 
                    } 
                    String name = query.substring(i + 1, j); 
                    c = '?'; // replace the parameter with a question mark 
                    i += name.length(); // skip past the end if the parameter 
 
                    List<Integer> indexList = paramMapAux.get(name); 
                    if (indexList == null) { 
                        indexList = new LinkedList<>(); 
                        paramMapAux.put(name, indexList); 
                    } 
                    indexList.add(index); 
 
                    index++; 
                } 
            } 
            parsedQuery.append(c); 
        } 
 
        // replace the lists of Integer objects with arrays of ints 
        for (Map.Entry<String, List<Integer>> entry : paramMapAux.entrySet()) { 
            List<Integer> list = entry.getValue(); 
            int[] indexes = new int[list.size()]; 
            int i = 0; 
            for (Integer x : list) { 
                indexes[i++] = x; 
            } 
            paramMap.put(entry.getKey(), indexes); 
        } 
 
        return parsedQuery.toString(); 
    } 
 
 
    /**
     * Returns the indexes for a parameter. 
     * 
     * @param name parameter name 
     * @return parameter indexes 
     * @throws IllegalArgumentException if the parameter does not exist 
     */ 
    private int[] getIndexes(String name) { 
        int[] indexes = indexMap.get(name); 
        if (indexes == null) { 
            throw new IllegalArgumentException("Parameter not found: " + name); 
        } 
        return indexes; 
    } 
 
 
    /**
     * Sets a parameter. 
     * 
     * @param name  parameter name 
     * @param value parameter value 
     * @throws SQLException             if an error occurred 
     * @throws IllegalArgumentException if the parameter does not exist 
     * @see PreparedStatement#setObject(int, Object)
     */ 
    public void setObject(String name, Object value) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            statement.setObject(indexes[i], value); 
        } 
    } 
 
    public void setObject(String name, Object value, int sqlType) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            statement.setObject(indexes[i], value, sqlType); 
        } 
    } 
 
 
    /**
     * Sets a parameter. 
     * 
     * @param name  parameter name 
     * @param value parameter value 
     * @throws SQLException             if an error occurred 
     * @throws IllegalArgumentException if the parameter does not exist 
     * @see PreparedStatement#setString(int, String)
     */ 
    public void setString(String name, String value) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            statement.setString(indexes[i], value); 
        } 
    } 
 
 
    /**
     * Sets a parameter. 
     * 
     * @param name  parameter name 
     * @param value parameter value 
     * @throws SQLException             if an error occurred 
     * @throws IllegalArgumentException if the parameter does not exist 
     * @see PreparedStatement#setInt(int, int) 
     */ 
    public void setInt(String name, int value) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            statement.setInt(indexes[i], value); 
        } 
    } 
 
 
    /**
     * Sets a parameter. 
     * 
     * @param name  parameter name 
     * @param value parameter value 
     * @throws SQLException             if an error occurred 
     * @throws IllegalArgumentException if the parameter does not exist 
     * @see PreparedStatement#setInt(int, int) 
     */ 
    public void setLong(String name, long value) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            statement.setLong(indexes[i], value); 
        } 
    } 
 
 
    /**
     * Sets a parameter. 
     * 
     * @param name  parameter name 
     * @param value parameter value 
     * @throws SQLException             if an error occurred 
     * @throws IllegalArgumentException if the parameter does not exist 
     * @see PreparedStatement#setTimestamp(int, Timestamp)
     */ 
    public void setTimestamp(String name, Timestamp value) throws SQLException { 
        int[] indexes = getIndexes(name); 
        for (int i = 0; i < indexes.length; i++) { 
            // TODO: Con setTimestamp contra Oracle en algunos casos los queries se quedaban colgados ... 
            //statement.setTimestamp(indexes[i], value); 
            Date date = new Date(value.getTime()); 
            statement.setDate(indexes[i], date); 
        } 
    } 
 
 
    /**
     * Returns the underlying statement. 
     * 
     * @return the statement 
     */ 
    public PreparedStatement getStatement() { 
        return statement; 
    } 
 
 
    /**
     * Executes the statement. 
     * 
     * @return true if the first result is a {@link ResultSet} 
     * @throws SQLException if an error occurred 
     * @see PreparedStatement#execute() 
     */ 
    public boolean execute() throws SQLException { 
        return statement.execute(); 
    } 
 
 
    /**
     * Executes the statement, which must be a query. 
     * 
     * @return the query results 
     * @throws SQLException if an error occurred 
     * @see PreparedStatement#executeQuery() 
     */ 
    public ResultSet executeQuery() throws SQLException { 
        if(statement.getMaxRows() >= 1000) { 
            statement.setFetchSize(1000); 
        } else { 
            statement.setFetchSize(statement.getMaxRows()); 
        } 
        return statement.executeQuery(); 
    } 
 
 
    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE statement; 
     * or an SQL statement that returns nothing, such as a DDL statement. 
     * 
     * @return number of rows affected 
     * @throws SQLException if an error occurred 
     * @see PreparedStatement#executeUpdate() 
     */ 
    public int executeUpdate() throws SQLException { 
        return statement.executeUpdate(); 
    } 
 
 
    /**
     * Closes the statement. 
     * 
     * @throws SQLException if an error occurred 
     * @see Statement#close() 
     */ 
    public void close() throws SQLException { 
        statement.close(); 
    } 
 
 
    /**
     * Adds the current set of parameters as a batch entry. 
     * 
     * @throws SQLException if something went wrong 
     */ 
    public void addBatch() throws SQLException { 
        statement.addBatch(); 
    } 
 
 
    /**
     * Executes all of the batched statements. 
     * * 
     * See {@link Statement#executeBatch()} for details. 
     * 
     * @return update counts for each statement 
     * @throws SQLException if something went wrong 
     */ 
    public int[] executeBatch() throws SQLException { 
        return statement.executeBatch(); 
    } 
 
    public NamedParameterStatement clonePure() throws SQLException { 
        return clonePure(getQuery()); 
    } 
 
    public NamedParameterStatement clonePure(String query) throws SQLException { 
        if (options != null) { 
            return new NamedParameterStatement(getStatement().getConnection(), query, options); 
        } else { 
            return new NamedParameterStatement(getStatement().getConnection(), query); 
        } 
    } 
}
