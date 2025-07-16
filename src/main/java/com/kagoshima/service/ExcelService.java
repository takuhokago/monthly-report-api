package com.kagoshima.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.YearMonth;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.kagoshima.entity.Report;

@Service
public class ExcelService {

	private ResourceLoader resourceLoader;

	@Autowired
	public ExcelService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Workbook createWorkbookWithReport(List<Report> reportList) throws IOException {
		// テンプレートファイルの読み込み
		Resource resource = resourceLoader.getResource("classpath:template_report.xlsx");
		InputStream inputStream = resource.getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);

		for (int i = 0; i < reportList.size(); i++) {
			// 書き込むReportの取得
			Report report = reportList.get(i);
			// シート名を更新
			String sheetName = report.getReportMonth().format(DateTimeFormatter.ofPattern("yyyyMM")) + "業務報告";
			workbook.setSheetName(3 + i, sheetName);
			// 書き込み先シートの取得
			Sheet sheet = workbook.getSheetAt(3 + i);
			// セルにデータの書き込み
			writeData(sheet, report);

			if (i != reportList.size() - 1) {
				// 次のReport用のシートを作成する
				Sheet copiedSheet = workbook.createSheet("CopiedSheet" + i);
				copyFixedRange(sheet, copiedSheet);
			}
		}

		return workbook;
	}

	private void writeData(Sheet sheet, Report report) {
		// 上揃え、左揃え
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.TOP); // 上揃え
		style.setAlignment(HorizontalAlignment.LEFT); // 左揃え
		style.setWrapText(true); // 折り返して全体を表示する設定

		// 上揃え、左揃え、上罫線
		CellStyle styleBorderTop = sheet.getWorkbook().createCellStyle();
		styleBorderTop.setVerticalAlignment(VerticalAlignment.TOP); // 上揃え
		styleBorderTop.setAlignment(HorizontalAlignment.LEFT); // 左揃え
		styleBorderTop.setWrapText(true); // 折り返して全体を表示する設定
		styleBorderTop.setBorderTop(BorderStyle.THIN); // 上罫線の設定

		// 中央揃え、上罫線
		CellStyle styleCenter = sheet.getWorkbook().createCellStyle();
		styleCenter.setVerticalAlignment(VerticalAlignment.CENTER); // 上揃え
		styleCenter.setAlignment(HorizontalAlignment.CENTER); // 左揃え
		styleCenter.setWrapText(true); // 折り返して全体を表示する設定
		styleCenter.setBorderTop(BorderStyle.THIN); // 上罫線の設定
		styleCenter.setBorderLeft(BorderStyle.THIN); // 左罫線の設定

		// 2行目
		Row row2 = sheet.getRow(1);
		if (row2 == null) {
			row2 = sheet.createRow(1);
		}

		// B2
		Cell cellB2 = row2.createCell(1);
		if (report.getReportMonth() != null) {
			cellB2.setCellValue(formatReportMonth(report.getReportMonth()));
		}
		cellB2.setCellStyle(styleCenter);

		// 4行目
		Row row4 = sheet.getRow(3);
		if (row4 == null) {
			row4 = sheet.createRow(3);
		}

		// C4
		Cell cellC4 = row4.createCell(2);
		if (report.getEmployee().getFullName() != null) {
			cellC4.setCellValue(report.getEmployee().getFullName());
		}
		cellC4.setCellStyle(styleCenter);

		// 7行目
		Row row7 = sheet.getRow(6);
		if (row7 == null) {
			row7 = sheet.createRow(6);
		}

		// C7
		Cell cellC7 = row7.createCell(2);
		if (report.getEmployee().getDepartment() != null) {
			cellC7.setCellValue(report.getEmployee().getDepartment().getName());
		}
		cellC7.setCellStyle(styleCenter);

		// 8行目
		Row row8 = sheet.getRow(7);
		if (row8 == null) {
			row8 = sheet.createRow(7);
		}

		// C8
		Cell cellC8 = row8.createCell(2);
		if (report.getContentBusiness() != null) {
			cellC8.setCellValue(report.getContentBusiness());
		}
		cellC8.setCellStyle(styleBorderTop);

		// 40行目
		Row row40 = sheet.getRow(39);
		if (row40 == null) {
			row40 = sheet.createRow(39);
		}

		// C40
		Cell cellC40 = row40.createCell(2);
		if (report.getTimeWorked() != null) {
			double timeWorkedInHours = report.getTimeWorked() / 60.0;
			double rounded = Math.round(timeWorkedInHours * 10.0) / 10.0; // 小数1桁に丸める
			cellC40.setCellValue(rounded);
		}
		cellC40.setCellStyle(styleCenter);

		// E40
		Cell cellE40 = row40.createCell(4);
		if (report.getTimeOver() != null) {
			double timeOverInHours = report.getTimeOver() / 60.0;
			double rounded = Math.round(timeOverInHours * 10.0) / 10.0; // 小数1桁に丸める
			cellE40.setCellValue(rounded);
		}
		cellE40.setCellStyle(styleCenter);

		// G40
		Cell cellG40 = row40.createCell(6);
		Integer rateBusiness = Optional.ofNullable(report.getRateBusiness()).orElse(-999);
		Integer rateStudy = Optional.ofNullable(report.getRateStudy()).orElse(-999);
		String rate = rateBusiness + " / " + rateStudy;
		cellG40.setCellValue(rate);
		cellG40.setCellStyle(styleCenter);

		// J40
		Cell cellJ40 = row40.createCell(9);
		if (report.getTrendBusiness() != null) {
			cellJ40.setCellValue(report.getTrendBusiness());
		}
		cellJ40.setCellStyle(styleCenter);

		// 41行目
		Row row41 = sheet.getRow(40);
		if (row41 == null) {
			row41 = sheet.createRow(40);
		}

		// C41
		Cell cellC41 = row41.createCell(2);
		if (report.getContentMember() != null) {
			cellC41.setCellValue(report.getContentMember());
		}
		cellC41.setCellStyle(styleBorderTop);

		// 62行目
		Row row62 = sheet.getRow(61);
		if (row62 == null) {
			row62 = sheet.createRow(61);
		}

		// C62
		Cell cellC62 = row62.createCell(2);
		if (report.getContentCustomer() != null) {
			cellC62.setCellValue(report.getContentCustomer());
		}
		cellC62.setCellStyle(style);

		// 70行目
		Row row70 = sheet.getRow(69);
		if (row70 == null) {
			row70 = sheet.createRow(69);
		}

		// C70
		Cell cellC70 = row70.createCell(2);
		if (report.getContentProblem() != null) {
			cellC70.setCellValue(report.getContentProblem());
		}
		cellC70.setCellStyle(style);

		// 81行目
		Row row81 = sheet.getRow(80);
		if (row81 == null) {
			row81 = sheet.createRow(80);
		}

		// D81
		Cell cellD81 = row81.createCell(3);
		if (report.getEvaluationBusiness() != null) {
			cellD81.setCellValue(report.getEvaluationBusiness());
		}
		cellD81.setCellStyle(style);

		// 84行目
		Row row84 = sheet.getRow(83);
		if (row84 == null) {
			row84 = sheet.createRow(83);
		}

		// D84
		Cell cellD84 = row84.createCell(3);
		if (report.getEvaluationStudy() != null) {
			cellD84.setCellValue(report.getEvaluationStudy());
		}
		cellD84.setCellStyle(style);

		// 87行目
		Row row87 = sheet.getRow(86);
		if (row87 == null) {
			row87 = sheet.createRow(86);
		}

		// D87
		Cell cellD87 = row87.createCell(3);
		if (report.getGoalBusiness() != null) {
			cellD87.setCellValue(report.getGoalBusiness());
		}
		cellD87.setCellStyle(style);

		// 90行目
		Row row90 = sheet.getRow(89);
		if (row90 == null) {
			row90 = sheet.createRow(89);
		}

		// D90
		Cell cellD90 = row90.createCell(3);
		if (report.getGoalStudy() != null) {
			cellD90.setCellValue(report.getGoalStudy());
		}
		cellD90.setCellStyle(style);

		// 93行目
		Row row93 = sheet.getRow(92);
		if (row93 == null) {
			row93 = sheet.createRow(92);
		}

		// C93
		Cell cellC93 = row93.createCell(2);
		if (report.getContentCompany() != null) {
			cellC93.setCellValue(report.getContentCompany());
		}
		cellC93.setCellStyle(style);

		// 99行目
		Row row99 = sheet.getRow(98);
		if (row99 == null) {
			row99 = sheet.createRow(98);
		}

		// C99
		Cell cellC99 = row99.createCell(2);
		if (report.getContentOthers() != null) {
			cellC99.setCellValue(report.getContentOthers());
		}
		cellC99.setCellStyle(style);

	}

	private String formatReportMonth(YearMonth reportMonth) {
		// 和暦のフォーマッターを作成（ロケールを日本に設定）
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Gyy年M月度", Locale.JAPAN)
				.withChronology(java.time.chrono.JapaneseChronology.INSTANCE);

		// 和暦の日付オブジェクトを作成
		JapaneseDate japaneseDate = JapaneseDate.of(reportMonth.getYear(), reportMonth.getMonthValue(), 1);

		// フォーマット適用
		return formatter.format(japaneseDate);
	}

	public String getFileName(Report report) {
		String templateFileName = "業務報告書_京都支社用(yearMonth_fullName)_Ver2.01.xlsx";
		String yearMonth = report.getReportMonth().format(DateTimeFormatter.ofPattern("yyyyMM"));
		String fullName = report.getEmployee().getFullName();
		String fileName = templateFileName.replace("yearMonth", yearMonth).replace("fullName", fullName);
		return fileName;
	}

	/**
	 * 指定された範囲のセル・スタイル・結合情報を元シートから新シートにコピーする
	 */
	private void copyFixedRange(Sheet source, Sheet target) {
		int startRow = 0;
		int endRow = 110;
		int startCol = 0;
		int endCol = 12;

		Workbook workbook = source.getWorkbook();
		Map<CellStyle, CellStyle> styleCache = new HashMap<>();
		Map<Short, Short> fontCache = new HashMap<>();

		// セルの内容・スタイルのコピー
		for (int i = startRow; i <= endRow; i++) {
			Row srcRow = source.getRow(i);
			Row destRow = target.createRow(i);

			if (srcRow != null) {
				destRow.setHeight(srcRow.getHeight()); // 行の高さをコピー

				for (int j = startCol; j <= endCol; j++) {
					Cell srcCell = srcRow.getCell(j);
					Cell destCell = destRow.createCell(j);

					if (srcCell != null) {
						copyCell(srcCell, destCell, workbook, styleCache, fontCache);
					}
				}
			}
		}

		// 列幅のコピー
		for (int i = startCol; i <= endCol; i++) {
			target.setColumnWidth(i, source.getColumnWidth(i));
		}

		// 結合セルの範囲をコピー
		for (int i = 0; i < source.getNumMergedRegions(); i++) {
			CellRangeAddress region = source.getMergedRegion(i);
			if (isWithinRange(region, startRow, endRow, startCol, endCol)) {
				target.addMergedRegion(region.copy());
			}
		}
	}

	/**
	 * 1つのセルの値・スタイル・リッチテキストをコピーする
	 */
	private void copyCell(Cell srcCell, Cell destCell, Workbook workbook, Map<CellStyle, CellStyle> styleCache,
			Map<Short, Short> fontCache) {

		// セルスタイルをキャッシュから再利用 or 新規作成
		CellStyle srcStyle = srcCell.getCellStyle();
		CellStyle newStyle = styleCache.get(srcStyle);

		if (newStyle == null) {
			newStyle = workbook.createCellStyle();
			newStyle.cloneStyleFrom(srcStyle);

			// フォントのコピー（キャッシュあり）
			Font srcFont = srcCell.getSheet().getWorkbook().getFontAt(srcStyle.getFontIndexAsInt());
			Font newFont = workbook.findFont(srcFont.getBold(), srcFont.getColor(), srcFont.getFontHeight(),
					srcFont.getFontName(), srcFont.getItalic(), srcFont.getStrikeout(), srcFont.getTypeOffset(),
					srcFont.getUnderline());

			if (newFont == null) {
				newFont = workbook.createFont();
				newFont.setBold(srcFont.getBold());
				newFont.setColor(srcFont.getColor());
				newFont.setFontHeight(srcFont.getFontHeight());
				newFont.setFontName(srcFont.getFontName());
				newFont.setItalic(srcFont.getItalic());
				newFont.setStrikeout(srcFont.getStrikeout());
				newFont.setTypeOffset(srcFont.getTypeOffset());
				newFont.setUnderline(srcFont.getUnderline());
			}

			newStyle.setFont(newFont);
			styleCache.put(srcStyle, newStyle);
		}

		destCell.setCellStyle(newStyle);

		// セルの型ごとに処理を分岐
		switch (srcCell.getCellType()) {
		case STRING:
			// リッチテキスト対応コピー（太字など）
			if (srcCell instanceof XSSFCell && destCell instanceof XSSFCell) {
				RichTextString richBase = srcCell.getRichStringCellValue();

				if (richBase instanceof XSSFRichTextString) {
					XSSFRichTextString richText = (XSSFRichTextString) richBase;
					XSSFRichTextString copiedRichText = new XSSFRichTextString(richText.getString());

					for (int i = 0; i < richText.numFormattingRuns(); i++) {
						int startIdx = richText.getIndexOfFormattingRun(i);
						int length = richText.getLengthOfFormattingRun(i);
						XSSFFont srcFont = richText.getFontOfFormattingRun(i);

						if (srcFont != null) {
							Font destFont = workbook.findFont(srcFont.getBold(), srcFont.getColor(),
									srcFont.getFontHeight(), srcFont.getFontName(), srcFont.getItalic(),
									srcFont.getStrikeout(), srcFont.getTypeOffset(), srcFont.getUnderline());

							if (destFont == null) {
								destFont = workbook.createFont();
								destFont.setBold(srcFont.getBold());
								destFont.setColor(srcFont.getColor());
								destFont.setFontHeight(srcFont.getFontHeight());
								destFont.setFontName(srcFont.getFontName());
								destFont.setItalic(srcFont.getItalic());
								destFont.setStrikeout(srcFont.getStrikeout());
								destFont.setTypeOffset(srcFont.getTypeOffset());
								destFont.setUnderline(srcFont.getUnderline());
							}

							copiedRichText.applyFont(startIdx, startIdx + length, destFont);
						}
					}

					((XSSFCell) destCell).setCellValue(copiedRichText);
				} else {
					destCell.setCellValue(srcCell.getStringCellValue());
				}
			} else {
				destCell.setCellValue(srcCell.getStringCellValue());
			}
			break;

		case NUMERIC:
			destCell.setCellValue(srcCell.getNumericCellValue());
			break;

		case BOOLEAN:
			destCell.setCellValue(srcCell.getBooleanCellValue());
			break;

		case FORMULA:
			destCell.setCellFormula(srcCell.getCellFormula());
			break;

		case ERROR:
			destCell.setCellErrorValue(srcCell.getErrorCellValue());
			break;

		case BLANK:
		default:
			// 空白または未対応の型は何もしない
			break;
		}

		// コメントのコピー（XSSFのみ対応）
		if (srcCell instanceof XSSFCell && destCell instanceof XSSFCell) {
			if (srcCell.getCellComment() != null) {
				XSSFComment srcComment = (XSSFComment) srcCell.getCellComment();
				XSSFDrawing drawing = ((XSSFSheet) destCell.getSheet()).createDrawingPatriarch();
				XSSFClientAnchor anchor = (XSSFClientAnchor) srcComment.getClientAnchor();

				XSSFClientAnchor newAnchor = new XSSFClientAnchor(anchor.getDx1(), anchor.getDy1(), anchor.getDx2(),
						anchor.getDy2(), anchor.getCol1(), anchor.getRow1(), anchor.getCol2(), anchor.getRow2());

				XSSFComment newComment = drawing.createCellComment(newAnchor);
				newComment.setString(srcComment.getString());
				newComment.setAuthor(srcComment.getAuthor());
				((XSSFCell) destCell).setCellComment(newComment);
			}
		}

	}

	/**
	 * 結合セルの範囲が、指定された行・列の範囲内に完全に収まっているか判定する
	 */
	private boolean isWithinRange(CellRangeAddress range, int startRow, int endRow, int startCol, int endCol) {
		return range.getFirstRow() >= startRow && range.getLastRow() <= endRow && range.getFirstColumn() >= startCol
				&& range.getLastColumn() <= endCol;
	}

}
