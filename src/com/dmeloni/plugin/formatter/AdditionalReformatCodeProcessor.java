package com.dmeloni.plugin.formatter;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

class AdditionalReformatCodeProcessor {

    private final Project project;

    private final Document document;

    AdditionalReformatCodeProcessor(Project project, Document document) {
        this.project = project;
        this.document = document;
    }

    public void run() {
        // Useful for undo action.
        Application application = ApplicationManager.getApplication();
        application.runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        AdditionalReformatCodeProcessor.format(document);
                    }
                }, "", "");
            }
        });
    }

    static public void format(Document document) {
        String documentText = document.getText();
        String[] split = documentText.split("\n");

        // Detects the if.
        for (int i = 1; i < split.length; i++) {

            if (AdditionalReformatCodeProcessor.isAStatement(split[i]) && (AdditionalReformatCodeProcessor.finishWithSemicolon(split[i - 1]) || AdditionalReformatCodeProcessor.finishWithBrace(split[i - 1]))) {
                split[i] = "\n" + split[i];
            }
        }

        // Rebuilds the lines.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            sb.append(split[i]);
            if (i != split.length - 1) {
                sb.append("\n");
            }
        }
        String joined = sb.toString();

        // Update the document.
        document.setText(joined);
    }

    static public boolean isIfStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "if (");
    }

    static public boolean isForeachStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "foreach (");
    }

    static public boolean isForStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "for (");
    }

    static public boolean isWhileStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "while (");
    }

    static public boolean isDoStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "do (");
    }

    static public boolean isCaseStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "case ");
    }

    static public boolean isPublicFunctionStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "public function ");
    }

    static public boolean isProtectedFunctionStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "protected function ");
    }

    static public boolean isPrivateFunctionStatement(String string) {
        return AdditionalReformatCodeProcessor.checkStatement(string, "private function ");
    }

    static public boolean isAStatement(String string) {
        return
                AdditionalReformatCodeProcessor.isCaseStatement(string) ||
                        AdditionalReformatCodeProcessor.isIfStatement(string) ||
                        AdditionalReformatCodeProcessor.isDoStatement(string) ||
                        AdditionalReformatCodeProcessor.isForeachStatement(string) ||
                        AdditionalReformatCodeProcessor.isForStatement(string) ||
                        AdditionalReformatCodeProcessor.isPublicFunctionStatement(string) ||
                        AdditionalReformatCodeProcessor.isProtectedFunctionStatement(string) ||
                        AdditionalReformatCodeProcessor.isPrivateFunctionStatement(string) ||
                        AdditionalReformatCodeProcessor.isWhileStatement(string);
    }

    static public boolean isBlankLine(String string) {
        String trimmedString = string.trim();

        return trimmedString.equals("");
    }

    static public boolean finishWithSemicolon(String string) {
        string = AdditionalReformatCodeProcessor.deleteSimpleComment(string);
        string = AdditionalReformatCodeProcessor.deleteComplexComment(string);

        String trimmedString = string.trim();

        return trimmedString.endsWith(";");
    }

    static public boolean finishWithBrace(String string) {
        string = AdditionalReformatCodeProcessor.deleteSimpleComment(string);
        string = AdditionalReformatCodeProcessor.deleteComplexComment(string);

        String trimmedString = string.trim();

        return trimmedString.endsWith("}");
    }

    static public String deleteSimpleComment(String string) {
        String trimmedString = string.trim();

        return trimmedString.split("//")[0];
    }

    static public String deleteComplexComment(String string) {
        String trimmedString = string.trim();

        return trimmedString.split("/\\*")[0];
    }

    static public boolean checkStatement(String string, String statement) {
        String trimmedString = string.trim();
        int statementLength = statement.length();

        return trimmedString.length() >= statementLength && trimmedString.substring(0, statementLength).equals(statement);
    }
}