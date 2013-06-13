package uk.co.volkswagen.contact.email

import org.grails.mandrill.ImageVar
import org.grails.mandrill.MandrillMessage
import org.grails.mandrill.MandrillRecipient
import org.grails.mandrill.MandrillRequest
import org.grails.mandrill.MergeVar

class EmailController {

    def grailsApplication, mandrillService

    def smokeTest() {
        def ret = mandrillService.ping()
        render """ PONG! means success, anything means we got a problem!
                  | Mandrill response;
                  | ${ret?.toString()}""".stripMargin()
    }

    def accountInfo() {
        def ret =  mandrillService.info()
        render ret?.toString()
    }

    def sendMail() {
        def recpts = []
        recpts.add(new MandrillRecipient(name:"foo", email:"brianfoody@gmail.com"))
        recpts.add(new MandrillRecipient(name:"bar", email:"brianfoody@gmail.clom"))
        def message = new MandrillMessage(
                text:"this is a text message",
                subject:"this is a subject",
                from_email:"no-reply@volkswagen.co.uk",
                to:recpts)
        message.tags.add("test")
        def ret = mandrillService.send(message)
        render ret?.toString()
    }

    def sendTemplatedMail() {
        def recpts = []
        recpts.add(new MandrillRecipient(name:"foo", email:"brianfoody@gmail.com"))
        recpts.add(new MandrillRecipient(name:"bar", email:"brianfoody@gmail.com"))

        def mergeVar = new MergeVar(name: "PRICE", content: "1333.33")

        def message = new MandrillMessage(
                text:"this is a text message",
                subject:"this is a subject",
                from_email:"no-reply@volkswagen.co.uk",
                merge: true,
                to:recpts)
        message.tags << "test"
        message.global_merge_vars << mergeVar

        def request = new MandrillRequest(template_name: 'vw-logo', message: message)
        def ret = mandrillService.sendTemplatedMail(request)
        render ret?.toString()
    }

    def sendTemplatedInlineImageMail() {
        def recpts = []
        recpts.add(new MandrillRecipient(name:"foo", email:"brianfoody@gmail.com"))
        recpts.add(new MandrillRecipient(name:"bar", email:"brianfoody@gmail.com"))

        def mergeVar = new MergeVar(name: "PRICE", content: "1333.33")

        def image = new URL(g.resource(dir: 'images', file: 'apple-touch-icon.png', absolute: true).toString())
        def imageVar = new ImageVar(name: "TEST", content: image.bytes.encodeBase64().toString())

        def message = new MandrillMessage(
                text:"this is a text message",
                subject:"this is a subject",
                from_email:"no-reply@volkswagen.co.uk",
                merge: true,
                to:recpts)
        message.tags << "test"
        message.global_merge_vars << mergeVar
        message.images << imageVar

        def request = new MandrillRequest(template_name: 'inline-image-test', message: message)
        def ret = mandrillService.sendTemplatedMail(request)
        render ret?.toString()
    }
}
