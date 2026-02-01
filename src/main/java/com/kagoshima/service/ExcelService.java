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

import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
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
			
			// 書き込み対象のシートを作成する
			Sheet writeSheet =  workbook.createSheet("WriteSheet" + i);
			
			// シート名を更新
			final int startSheetIndex = 7;
			String sheetName = report.getReportMonth().format(DateTimeFormatter.ofPattern("yyyyMM")) + "業務報告";
			workbook.setSheetName(startSheetIndex + i, sheetName);
			
			// 大小どちらのテンプレートを使用するか判定
			boolean isLargeContentBusiness= report.getContentBusiness().length() > 700;
			boolean isLargeContentMember= report.getContentMember().length() > 500;
			
			// テンプレートをコピーする
			final int largeLargeIndex = 3;
			final int smallSmallIndex = 4;
			final int largeSmallIndex = 5;
			final int smallLargeIndex = 6;
			if(isLargeContentBusiness && isLargeContentMember) {
				Sheet template = workbook.getSheetAt(largeLargeIndex);
				copyFixedRange(template, writeSheet);
			} else if(!isLargeContentBusiness && !isLargeContentMember) {
				Sheet template = workbook.getSheetAt(smallSmallIndex);
				copyFixedRange(template, writeSheet);
			} else if(isLargeContentBusiness) {
				Sheet template = workbook.getSheetAt(largeSmallIndex);
				copyFixedRange(template, writeSheet);
			} else {
				Sheet template = workbook.getSheetAt(smallLargeIndex);
				copyFixedRange(template, writeSheet);
			}
			
			// セルにデータの書き込み
			writeData(writeSheet, report);
			
			// 最後にテンプレートは削除する
			if(i == reportList.size() - 1) {
				workbook.removeSheetAt(smallLargeIndex);
				workbook.removeSheetAt(largeSmallIndex);
				workbook.removeSheetAt(smallSmallIndex);
				workbook.removeSheetAt(largeLargeIndex);
			}
		}

		return workbook;
	}

	private void writeData(Sheet sheet, Report report) {
		// 先に取得しておく
		Employee emp = report.getEmployee();
		Department department = emp.getDepartment();
		
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
		if (emp.getFullName() != null) {
			cellC4.setCellValue(emp.getFullName());
		}
		cellC4.setCellStyle(styleCenter);

		// 7行目
		Row row7 = sheet.getRow(6);
		if (row7 == null) {
			row7 = sheet.createRow(6);
		}

		// C7
		Cell cellC7 = row7.createCell(2);
		if (department != null) {
			cellC7.setCellValue(department.getName());
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

		// 24行目
		Row row24 = sheet.getRow(23);
		if (row24 == null) {
			row24 = sheet.createRow(23);
		}

		// C24
		Cell cellC24 = row24.createCell(2);
		if (report.getTimeWorked() != null) {
			double timeWorkedInHours = report.getTimeWorked() / 60.0;
			double rounded = Math.round(timeWorkedInHours * 10.0) / 10.0; // 小数1桁に丸める
			cellC24.setCellValue(rounded);
		}
		cellC24.setCellStyle(styleCenter);

		// E24
		Cell cellE24 = row24.createCell(4);
		if (report.getTimeOver() != null) {
			double timeOverInHours = report.getTimeOver() / 60.0;
			double rounded = Math.round(timeOverInHours * 10.0) / 10.0; // 小数1桁に丸める
			cellE24.setCellValue(rounded);
		}
		cellE24.setCellStyle(styleCenter);

		// G24
		Cell cellG24 = row24.createCell(6);
		Integer rateBusiness = Optional.ofNullable(report.getRateBusiness()).orElse(-999);
		Integer rateStudy = Optional.ofNullable(report.getRateStudy()).orElse(-999);
		String rate = rateBusiness + " / " + rateStudy;
		cellG24.setCellValue(rate);
		cellG24.setCellStyle(styleCenter);

		// J24
		Cell cellJ24 = row24.createCell(9);
		if (report.getTrendBusiness() != null) {
			cellJ24.setCellValue(report.getTrendBusiness());
		}
		cellJ24.setCellStyle(styleCenter);

		// 25行目
		Row row25 = sheet.getRow(24);
		if (row25 == null) {
			row25 = sheet.createRow(24);
		}

		// C25
		Cell cellC25 = row25.createCell(2);
		if (report.getContentMember() != null) {
			cellC25.setCellValue(report.getContentMember());
		}
		cellC25.setCellStyle(styleBorderTop);

		// 40行目
		Row row40 = sheet.getRow(39);
		if (row40 == null) {
			row40 = sheet.createRow(39);
		}

		// C40
		Cell cellC40 = row40.createCell(2);
		if (report.getContentCustomer() != null) {
			cellC40.setCellValue(report.getContentCustomer());
		}
		cellC40.setCellStyle(style);

		// 48行目
		Row row48 = sheet.getRow(47);
		if (row48 == null) {
			row48 = sheet.createRow(47);
		}

		// C48
		Cell cellC48 = row48.createCell(2);
		if (report.getContentProblem() != null) {
			cellC48.setCellValue(report.getContentProblem());
		}
		cellC48.setCellStyle(style);

		// 59行目
		Row row59 = sheet.getRow(58);
		if (row59 == null) {
			row59 = sheet.createRow(58);
		}

		// D59
		Cell cellD59 = row59.createCell(3);
		if (report.getEvaluationBusiness() != null) {
			cellD59.setCellValue(report.getEvaluationBusiness());
		}
		cellD59.setCellStyle(style);

		// 62行目
		Row row62 = sheet.getRow(61);
		if (row62 == null) {
			row62 = sheet.createRow(61);
		}

		// D62
		Cell cellD62 = row62.createCell(3);
		if (report.getEvaluationStudy() != null) {
			cellD62.setCellValue(report.getEvaluationStudy());
		}
		cellD62.setCellStyle(style);

		// 65行目
		Row row65 = sheet.getRow(64);
		if (row65 == null) {
			row65 = sheet.createRow(64);
		}

		// D65
		Cell cellD65 = row65.createCell(3);
		if (report.getGoalBusiness() != null) {
			cellD65.setCellValue(report.getGoalBusiness());
		}
		cellD65.setCellStyle(style);

		// 68行目
		Row row68 = sheet.getRow(67);
		if (row68 == null) {
			row68 = sheet.createRow(67);
		}

		// D68
		Cell cellD68 = row68.createCell(3);
		if (report.getGoalStudy() != null) {
			cellD68.setCellValue(report.getGoalStudy());
		}
		cellD68.setCellStyle(style);

		// 71行目
		Row row71 = sheet.getRow(70);
		if (row71 == null) {
			row71 = sheet.createRow(70);
		}

		// C71
		Cell cellC71 = row71.createCell(2);
		if (report.getContentCompany() != null) {
			cellC71.setCellValue(report.getContentCompany());
		}
		cellC71.setCellStyle(style);

		// 77行目
		Row row77 = sheet.getRow(76);
		if (row77 == null) {
			row77 = sheet.createRow(76);
		}

		// C77
		Cell cellC77 = row77.createCell(2);
		if (report.getContentOthers() != null) {
			cellC77.setCellValue(report.getContentOthers());
		}
		cellC77.setCellStyle(style);

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
