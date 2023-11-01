package com.example.live;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NoteFileController {

    private final NoteRepository noteRepository;
    private final FileRepository fileRepository;

    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        List<Note> notes = noteRepository.findAll();
        model.addAttribute("notes", notes);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("filename") String filenameWithoutExtension,
                             @RequestParam("record") String record,
                             @RequestParam("title") String title) {
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFilename = filenameWithoutExtension + fileExtension;

                File fileEntity = new File();
                fileEntity.setFileName(newFilename);
                fileEntity.setFileData(file.getBytes());
                fileEntity.setFileSize(file.getSize());
                fileRepository.save(fileEntity);
                Note newNote = new Note();
                newNote.setFile(fileEntity);
                newNote.setRecord(record);
                newNote.setTitle(title);
                noteRepository.save(newNote);
                return "redirect:/";
            } catch (IOException e) {
                log.error("Ошибка обработки файла", e);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> serveFile(@PathVariable Long id) {
        Optional<File> fileEntityOptional = fileRepository.findById(id);
        if (fileEntityOptional.isPresent()) {
            File fileEntity = fileEntityOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"");
            return new ResponseEntity<>(fileEntity.getFileData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
        return "redirect:/";
    }
}