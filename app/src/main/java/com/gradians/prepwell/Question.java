package com.gradians.prepwell;

import android.content.Context;
import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by adamarla on 19/1/15.
 */
public class Question {


    protected Question(Document tree) {
        this.tree = tree;
    }

    public Document getTree() {
        return tree;
    }

    public String getId() {
        return tree.getElementsByTagName("id").item(0).getTextContent();
    }

    public String getTopicId() {
        return tree.getElementsByTagName("topicId").item(0).getTextContent();
    }

    public String getProblemStatement() {
        Element statement = (Element)tree.getElementsByTagName("statement").item(0);
        NodeList images = statement.getElementsByTagName("image");
        if (images.getLength() == 0) {
            return statement.getElementsByTagName("latex").item(0).getTextContent();
        } else {
            return images.item(0).getTextContent();
        }
    }

    public int numSolutions() {
        return tree.getElementsByTagName("solution").getLength();
    }

    public String getSolutionStatement(int solnIdx) {
        Element solution = (Element)tree.getElementsByTagName("solution").item(solnIdx);
        if (solution.getElementsByTagName("statement").getLength() == 0)
            return  null;
        else {
            Element statement = (Element)solution.getElementsByTagName("statement").item(0);
            NodeList images = statement.getElementsByTagName("image");
            if (images.getLength() == 0) {
                return statement.getElementsByTagName("latex").item(0).getTextContent();
            } else {
                return images.item(0).getTextContent();
            }
        }
    }

    public int numParts(int solnIdx) {
        Element solution = (Element)tree.getElementsByTagName("solution").item(solnIdx);
        return solution.getElementsByTagName("part").getLength();
    }

    public int numSteps(int solnIdx, int partIdx) {
        Element solution = (Element)tree.getElementsByTagName("solution").item(solnIdx);
        Element part = (Element)solution.getElementsByTagName("part").item(partIdx);
        return part.getElementsByTagName("step").getLength();
    }

    public String[] getOptions(int solnIdx, int partIdx, int stepIdx) {
        Element solution = (Element)tree.getElementsByTagName("solution").item(solnIdx);
        Element part = (Element)solution.getElementsByTagName("part").item(partIdx);
        Element step = (Element)part.getElementsByTagName("step").item(stepIdx);
        NodeList nodeList = step.getElementsByTagName("option");
        String[] options = new String[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element option = (Element)nodeList.item(i);
            NodeList images = option.getElementsByTagName("image");
            if (images.getLength() == 0) {
                options[i] = option.getElementsByTagName("latex").item(0).getTextContent();
            } else {
                options[i] = images.item(0).getTextContent();
            }
        }
        return options;
    }

    public String[] getAnswers() {
        NodeList nodeList = tree.getElementsByTagName("answer");
        String[] answers = new String[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element answer = (Element)nodeList.item(i);
            answers[i] = answer.getElementsByTagName("latex").item(0).getTextContent();
        }
        return answers;
    }

    private Document tree;

}

class QuestionBuilder {

    public QuestionBuilder(Context context) {
        this.context = context;
    }

    public Question build(String problemId) {
        Document tree = null;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream xml = assetManager.open("problems/" + problemId + "/question.xml");
            DocumentBuilder builder = getParser();
            tree = builder.parse(xml);
        } catch (Exception e) { }
        return new Question(tree);
    }

    private DocumentBuilder getParser() {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (Exception e) { }
        return builder;
    }

    private Context context;
}