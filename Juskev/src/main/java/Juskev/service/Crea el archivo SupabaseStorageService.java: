package Juskev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BUCKET = "productos";

    public String subirImagen(MultipartFile file) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET + "/" + nombreArchivo;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceKey);
        headers.setContentType(MediaType.parseMediaType(
            file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        ));

        HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);
        restTemplate.exchange(uploadUrl, HttpMethod.POST, request, String.class);

        // URL pública del archivo
        return supabaseUrl + "/storage/v1/object/public/" + BUCKET + "/" + nombreArchivo;
    }
}
