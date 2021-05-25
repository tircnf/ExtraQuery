package com

import grails.persistence.Entity
import grails.test.hibernate.HibernateSpec
import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture

class ExtraQuerySpec extends HibernateSpec {

    @Rule
    OutputCapture capture = new OutputCapture()


    @Override
    List<Class> getDomainClasses() {
        return [Pet, NickName]
    }


    void testSetup() {
        expect:
        true
        !capture.toString()
    }

    void testLogging() {
        expect:
        new Pet(name: "jerry").save(flush: true, failOnError: true)
        capture.toString().contains("insert into pet")
    }

    void testCriteria() {
        when: "I create a detachedCriteria"
        Pet.where {

        }
        then: "no query is executed"
        !capture.toString()
    }

    void "test and query join criteria."() {
        when: "I create query with join and projection"
        Pet.where {
            def tags = nickNames
            tags.nickname == "Spot"

            order("tags.nickname")

        }

        then: "No query should have been executed, but count(*) from nick_name runs."
        !capture.toString()
    }
}

@Entity
class Pet {

    static hasMany = [nickNames: NickName]
    String name

}

@Entity
class NickName {
    static belongsTo = [pet: Pet]
    String nickname
}
