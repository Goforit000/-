package com.example.service;

import com.example.mapper.CostEvaluationMapper;
import com.example.entity.CostEvaluation;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class WordExportService {

    @Autowired
    private CostEvaluationMapper costEvaluationMapper;

    /**
     * 通过 big_project_id 生成 Word 文件，每个子系统一个表格
     *
     * @param bigProjectId 项目ID
     * @return Word 文件的字节数组
     * @throws IOException 如果生成 Word 文件时发生错误
     */
    public byte[] generateWordFile(int bigProjectId) throws IOException {
        // 从数据库获取项目信息和子系统信息
        List<CostEvaluation> evaluations = costEvaluationMapper.selectSubsystemByBigProjectId(bigProjectId);

        // 创建一个空的 Word 文档
        XWPFDocument document = new XWPFDocument();

        // 添加标题
        XWPFParagraph title = document.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("造价评估结果");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.addBreak();

        // 为每个子系统生成一个表格
        for (CostEvaluation evaluation : evaluations) {
            // 每个子系统输出一个独立的表格
            XWPFTable table = document.createTable();

            // 创建表格的表头，第一行是评估字段
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("评估字段");
            headerRow.addNewTableCell().setText("值");

            // 子系统信息表格，逐行添加数据
            addRowToTable(table, "子系统 ID", String.valueOf(evaluation.getProjectId()));
            addRowToTable(table, "项目名称", evaluation.getProjectName());
            addRowToTable(table, "子系统名称", evaluation.getSubsystemName());
            addRowToTable(table, "子系统描述", evaluation.getSubsystemDescribe());
            addRowToTable(table, "调整前的功能点数", String.valueOf(evaluation.getUnadjustedFp()));
            addRowToTable(table, "调整后的功能点数", String.valueOf(evaluation.getAdjustedFP()));
            addRowToTable(table, "功能点耗时率上限", String.valueOf(evaluation.getUpperPDR()));
            addRowToTable(table, "功能点耗时率中值", String.valueOf(evaluation.getMidPDR()));
            addRowToTable(table, "功能点耗时率下限", String.valueOf(evaluation.getFloorPDR()));
            addRowToTable(table, "业务领域调整因子", String.valueOf(evaluation.getBusinessDomain()));
            addRowToTable(table, "应用类型调整因子", String.valueOf(evaluation.getApplicationType()));
            addRowToTable(table, "质量特性调整因子", String.valueOf(evaluation.getQualityCharacteristic()));
            addRowToTable(table, "开发语言调整因子", String.valueOf(evaluation.getDevelopLanguage()));
            addRowToTable(table, "开发团队背景因子", String.valueOf(evaluation.getTeamBackground()));
            addRowToTable(table, "风险调整因子", String.valueOf(evaluation.getRisk()));
            addRowToTable(table, "非人工成本调整因子", String.valueOf(evaluation.getDirectNonhumanCost()));
            addRowToTable(table, "人工成本费率", String.valueOf(evaluation.getLaborCostRate()));
            addRowToTable(table, "直质量等级因子", String.valueOf(evaluation.getStraightQualityGrade()));
            addRowToTable(table, "调整后的估算工作量上限", String.valueOf(evaluation.getUpperWorkload()));
            addRowToTable(table, "调整后的估算工作量中值", String.valueOf(evaluation.getMidWorkload()));
            addRowToTable(table, "调整后的估算工作量下限", String.valueOf(evaluation.getFloorWorkload()));
            addRowToTable(table, "软件开发成本上限", String.valueOf(evaluation.getUpperCost()));
            addRowToTable(table, "软件开发成本中值", String.valueOf(evaluation.getMidCost()));
            addRowToTable(table, "软件开发成本下限", String.valueOf(evaluation.getFloorCost()));
            addRowToTable(table, "调整后的估算工作量上限", String.valueOf(evaluation.getUpperAdjustedCost()));
            addRowToTable(table, "调整后的估算工作量中值", String.valueOf(evaluation.getMidAdjustedCost()));
            addRowToTable(table, "调整后的估算工作量下限", String.valueOf(evaluation.getFloorAdjustedCost()));

            // 添加一个空行，用于分隔子系统
            XWPFParagraph emptyParagraph = document.createParagraph();
            emptyParagraph.createRun().addBreak();
        }

        // 输出到字节流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);

        // 返回字节数组
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 用于将每个评估字段和评估数据填充到表格的行中
     */
    private void addRowToTable(XWPFTable table, String fieldName, String fieldValue) {
        XWPFTableRow row = table.createRow();

        // 设置评估字段名称列
        XWPFTableCell cell0 = row.getCell(0);
        cell0.setText(fieldName);  // 第一列是评估字段的名称
        XWPFParagraph paragraph0 = cell0.getParagraphs().get(0);
        paragraph0.setSpacingAfter(200);  // 设置该单元格行间距（单位为磅，200表示较大的行间距）

        // 设置评估值列
        XWPFTableCell cell1 = row.getCell(1);
        cell1.setText(fieldValue);  // 第二列是评估值
        XWPFParagraph paragraph1 = cell1.getParagraphs().get(0);
        paragraph1.setSpacingAfter(200);  // 设置该单元格行间距（单位为磅）
    }
}
