package com.zhengenshen.plugin.translation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.apache.http.util.TextUtils;

/**
 * 开始
 *
 * @author zhengenshen
 * @create 2018-02-05 10:43
 */

public class TranslationAction extends AnAction {


    /**
     * 执行插件的入口，相当于java中的main方法
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String basePath = project != null ? project.getBasePath() : null;

        if (null == editor) {
            return;
        }

        SelectionModel model = editor.getSelectionModel();
        String selectedText = model.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            selectedText = getCurrentWords(editor);
            if (TextUtils.isEmpty(selectedText)) {
                return;
            }
        }

        String queryText = strip(addBlanks(selectedText));

        new Thread(new GoogleTranslate(editor, queryText, basePath)).start();

        // Messages.showMessageDialog(project, selectedText, "Welcome", Messages.getInformationIcon());
    }


    private String getCurrentWords(Editor editor) {
        Document document = editor.getDocument();

        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        String text = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        char[] chars = text.toCharArray();
        int start = 0, end = 0, cursor = offset - lineStartOffset;
        if (!Character.isLetter(chars[cursor])) {
            System.out.println("Caret not in a word");
            return null;
        }
        for (int ptr = cursor; ptr >= 0; ptr--) {
            if (!Character.isLetter(chars[cursor])) {
                start = ptr + 1;
                break;
            }
        }

        int lastLetter = 0;
        for (int ptr = cursor; ptr < lineEndOffset - lineStartOffset; ptr++) {
            lastLetter = ptr;
            if (!Character.isLetter(chars[cursor])) {
                end = ptr;
                break;
            }
        }
        if (end == 0) {
            end = lastLetter + 1;
        }
        String ret = new String(chars, start, end - start);
        System.out.println("selected words " + ret);
        return ret;
    }

    private String addBlanks(String str) {
        String temp = str.replaceAll("_", " ");
        if (temp.equals(temp.toUpperCase())) {
            return temp;
        }
        String result = temp.replaceAll("([A-z]+)", "$0");
        return result;
    }

    private String strip(String s) {
        return s.replaceAll("/\\*+", "")
                .replaceAll("\\*+/", "")
                .replaceAll("\\*", "")
                .replaceAll("//+", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\\s+", " ");
    }
}
