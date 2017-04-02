package com.dmeloni.plugin.formatter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;

public class ReformatAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Document document = e.getData(PlatformDataKeys.EDITOR).getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);

        // Legacy processor.
        ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(project, psiFile, null, true);
        reformatCodeProcessor.run();

        if (this.isEligiblePhpFile(project, psiFile)) {
            // Additional processors.
            AdditionalReformatCodeProcessor additionalReformatCodeProcessor = new AdditionalReformatCodeProcessor(project, document);
            additionalReformatCodeProcessor.run();
        }
    }

    public boolean isEligiblePhpFile(Project project, PsiFile psiFile) {
        boolean isPhpFile = psiFile.getVirtualFile().getCanonicalPath().endsWith(".php");
        boolean isInProject = ProjectRootManager.getInstance(project).getFileIndex().isInContent(psiFile.getVirtualFile());

        return project.isInitialized() &&
                !project.isDisposed() &&
                isInProject &&
                isPhpFile &&
                psiFile.getModificationStamp() != 0;
    }
}
