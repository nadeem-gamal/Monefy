package com.monefy.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

	/**
	 * Reads CSV file and returns parsed data as a list of maps
	 * 
	 * @param filePath the path to the CSV file
	 * @return List of maps containing CSV data
	 */
	public List<Map<String, String>> readCSVFile(String filePath) {
		List<Map<String, String>> data = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String headerLine = reader.readLine();
			if (headerLine == null) {
				return data;
			}

			String[] headers = parseCSVLine(headerLine);
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] values = parseCSVLine(line);
				Map<String, String> row = new HashMap<>();

				for (int i = 0; i < headers.length && i < values.length; i++) {
					row.put(headers[i].trim(), values[i].trim());
				}

				data.add(row);
			}
		} catch (IOException e) {
			System.err.println("Error reading CSV file: " + e.getMessage());
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Parses a CSV line handling quoted values
	 * 
	 * @param line the CSV line to parse
	 * @return array of parsed values
	 */
	private String[] parseCSVLine(String line) {
		List<String> result = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean insideQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			char nextChar = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';

			if (c == '"') {
				if (insideQuotes && nextChar == '"') {
					current.append('"');
					i++;
				} else {
					insideQuotes = !insideQuotes;
				}
			} else if (c == ',' && !insideQuotes) {
				result.add(current.toString());
				current = new StringBuilder();
			} else {
				current.append(c);
			}
		}
		result.add(current.toString());

		return result.toArray(new String[0]);
	}

	/**
	 * Reads CSV file from uploaded MultipartFile
	 * 
	 * @param file the uploaded CSV file
	 * @return List of maps containing CSV data
	 */
	public List<Map<String, String>> readCSVFromUpload(MultipartFile file) {
		List<Map<String, String>> data = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(convertMultipartToFile(file)));
			String headerLine = reader.readLine();
			if (headerLine == null) {
				reader.close();
				return data;
			}

			String[] headers = parseCSVLine(headerLine);
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] values = parseCSVLine(line);
				Map<String, String> row = new HashMap<>();

				for (int i = 0; i < headers.length && i < values.length; i++) {
					row.put(headers[i].trim(), values[i].trim());
				}

				data.add(row);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading uploaded CSV file: " + e.getMessage());
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Converts MultipartFile to File
	 * 
	 * @param file the MultipartFile to convert
	 * @return converted File
	 */
	private File convertMultipartToFile(MultipartFile file) throws IOException {
		File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
		file.transferTo(tempFile);
		return tempFile;
	}

	@GetMapping
	public List<?> getAllStatistics() {
		System.out.println("Start");
		List<Map<String, String>> csvFile = readCSVFile("Monefy.Data.1-14-26.csv");
		System.out.println(csvFile.size());
		System.out.println("End");
//		List<Map<String, String>> result = csvFile.stream().filter(x -> x.get("category").equals("Clothes"))
//		List<Map<String, String>> result = csvFile.stream().filter(x -> x.get("description").contains("فلتر"))
//				.filter(x -> x.get("date").contains("2025")).toList();
//		System.out.println("Sum = " + result.stream().mapToDouble(x -> Double.parseDouble(x.get("amount").replace(",", ""))).sum());
//		return result;
		return csvFile;
	}

	/**
	 * API endpoint to upload and process CSV file
	 * 
	 * @param file the CSV file to upload
	 * @return List of processed CSV data
	 */
	@PostMapping("/upload")
	public List<Map<String, String>> uploadCSV(@RequestParam("file") MultipartFile file) {
		return readCSVFromUpload(file);
	}

	/**
	 * API endpoint to read CSV from default file path
	 * 
	 * @param filePath the path to the CSV file
	 * @return List of processed CSV data
	 */
	@GetMapping("/read")
	public List<Map<String, String>> readCSV(@RequestParam String filePath) {
		return readCSVFile(filePath);
	}

}