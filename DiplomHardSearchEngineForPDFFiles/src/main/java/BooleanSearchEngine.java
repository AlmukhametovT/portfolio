import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    private final Set<String> allWords;
    private final Map<String, List<PageEntry>> searchResult;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        File[] files = pdfsDir.listFiles();
        this.allWords = setAllWords(files);
        this.searchResult = setSearchResult(files, allWords);
    }

    @Override
    public List<PageEntry> search(String searchWord) {
        // тут реализуйте поиск по слову
        String word = searchWord.toLowerCase();
//        System.out.println(searchWord + " = " + word);

        if (!allWords.contains(word)) {
            return Collections.emptyList();
        }

        List<PageEntry> result = searchResult.get(word);
        return result.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public Set<String> setAllWords(File[] files) throws IOException {
        Set<String> allWords = new HashSet<>();
        for (int i = 0; i < files.length; i++) {
            var doc = new PdfDocument(new PdfReader(files[i]));
            int pages = doc.getNumberOfPages();
            for (int j = 1; j < pages + 1; j++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(j));
                var words = text.split("\\P{IsAlphabetic}+");
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    allWords.add(word.toLowerCase());
                }
            }
        }
        return allWords;
    }

    public Map<String, List<PageEntry>> setSearchResult(File[] files, Set<String> allWords) throws IOException {
        Map<String, List<PageEntry>> searchResult = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            var doc = new PdfDocument(new PdfReader(files[i]));
            int pages = doc.getNumberOfPages();
            for (int j = 1; j < pages + 1; j++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(j));
                var words = text.split("\\P{IsAlphabetic}+");
                for (int k = 0; k < words.length; k++) {
                    String tempLowerCaseWord = words[k].toLowerCase();
                    words[k] = tempLowerCaseWord;
                }
                List wordsList = new ArrayList<>(Arrays.asList(words));
                for (String word : allWords) {
                    if (!wordsList.contains(word)) {
                        continue;
                    }
                    int count = 0;
                    for (int k = 0; k < wordsList.size(); k++) {
                        if (wordsList.get(k).equals(word)) {
                            count++;
                        }
                    }
                    PageEntry pageEntry = new PageEntry(files[i].getName(), j, count);
                    if (!searchResult.containsKey(word)) {
                        List<PageEntry> newListPageEntry = new ArrayList<>();
                        searchResult.put(word, newListPageEntry);
                    }
                    List<PageEntry> listPageEntry = searchResult.get(word);
                    listPageEntry.add(pageEntry);
                    searchResult.put(word, listPageEntry);
                }
            }
        }
        return searchResult;
    }

    public Set<String> getAllWords() {
        return allWords;
    }
}