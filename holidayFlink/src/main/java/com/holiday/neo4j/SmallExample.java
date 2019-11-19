package com.holiday.neo4j;


import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;


import java.util.Iterator;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class SmallExample {
    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;

    public SmallExample(String uri, String user, String password) {
        // 获取数据库连接
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    // 创建节点
    private void addPerson(String name) {
        // Sessions are lightweight and disposable connection wrappers.
        // 获取session
        try (Session session = driver.session()) {
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
            // 创建一个事物
            try (Transaction tx = session.beginTransaction()) {
                //执行一个语句
                tx.run("create (a:Holiday {name: {x}})", parameters("x", name));
                //提交成功
                tx.success();  // Mark this write as successful.
            }
        }
    }

    private void printPeople(String initial) {
        try (Session session = driver.session()) {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH (a:Holiday) WHERE a.name STARTS WITH {x} RETURN a.name AS name",
                    parameters("x", initial));
            // Each Cypher execution returns a stream of records.
            while (result.hasNext()) {
                //是一行记录，内容是什么取决于你的 return  result
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record);
                // 获取属性值是通过  RETURN a.name AS name  别名
                System.out.println(record.get("name").asString());
            }
        }
    }

    public void close() {
        // Closing a driver immediately shuts down all open connections.
        driver.close();
    }


    // 获取所有数据与属性值
    private void getPeople() {
        try (Session session = driver.session()) {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH (a:Holiday)  RETURN a ");
            // Each Cypher execution returns a stream of records.
            //循环每行数据
            while (result.hasNext()) {
                //是一行记录，内容是什么取决于你的 return  result
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record);
                //获取每行数据的(node<38>)  Record<{a: node<38>}>
                List<Value> list = record.values();
                for (Value v : list) {
                    Node n = v.asNode();
                    //获取label
                    String label = n.labels().iterator().next();
                    // 获取数据的伪id
                    Long id = n.id();
                    // 循环获取每行数据的属性
                    for (String s : n.keys()) {
                        //通过key 获取属性值
                        System.out.println(n.get(s));
                    }
                }
            }
        }
    }

    // 获取关系
    private void getRelation() {
        try (Session session = driver.session()) {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH p=(b:Holiday)-[]->(c) RETURN p ");
            // Each Cypher execution returns a stream of records.
            //循环每行数据
            while (result.hasNext()) {
                //是一行记录，内容是什么取决于你的 return  result
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record);
                //获取每行数据的(node<38>)  Record<{a: node<38>}>
                List<Value> list = record.values();
                for (Value v : list) {
                    //转为关系
                    Path path = v.asPath();
                    //获取关系内容
                    Node start = path.start();
                    Node end = path.end();
                    //得到关系开始的 属性值
                    for (String k : start.keys()) {
                        System.out.println("start----" + start.get(k));
                    }
                    //得到关系
                    Iterator<Relationship> i = path.relationships().iterator();
                    while (i.hasNext()) {
                        // 得到关系 id i.next()
                        //   System.out.println(i.next());
                        //得到关系名称 Friend r.type()
                        Relationship r = i.next();
                        System.out.println(r.type());
                        // 得到关系的指向，从谁到谁,id
                        System.out.println(r.startNodeId() + "---->" + r.endNodeId());
                    }
                    //得到关系结束的 属性值
                    for (String k : end.keys()) {
                        System.out.println("end----" + end.get(k));
                    }
                }
            }
        }
    }

    public static void main(String... args) {
        SmallExample example = new SmallExample("bolt://192.168.226.128:7687", "neo4j", "admin123");
        //example.addPerson("Ada");
        // example.addPerson("Alice");
        //example.addPerson("Bob");
        example.getRelation();
        example.close();
    }
}