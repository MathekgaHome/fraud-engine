//package za.co.fraud.fraud_service.services.storage.minio;
//
//import io.minio.GetObjectArgs;
//import io.minio.MinioClient;
//import io.minio.PutObjectArgs;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import za.co.fraud.fraud_service.services.storage.StorageProvider;
//
//import java.io.InputStream;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MinioStorageProvider implements StorageProvider {
//
//    private final MinioClient minioClient;
//
//    @Value("${storage.minio.bucket}")
//    private String bucket;
//
//    @Override
//    public String upload(InputStream inputStream, long fileSize, String path, String fileName, String contentType) {
//        String objectName = path + "/" + UUID.randomUUID() + "-" + fileName;
//        try {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(objectName)
//                            // Stream direct - no byte arrays allowed!
//                            .stream(inputStream, fileSize, -1)
//                            .contentType(contentType)
//                            .build()
//            );
//            return objectName;
//        } catch (Exception e) {
//            log.error("MinIO Fraud Document Upload Failed: {}", e.getMessage());
//            throw new RuntimeException("Upload failed", e);
//        }
//    }
//
//    @Override
//    public InputStream download(String providerKey) {
//        try {
//            return minioClient.getObject(
//                    GetObjectArgs.builder().bucket(bucket).object(providerKey).build());
//        } catch (Exception e) {
//            log.error("Download failed: {}", e.getMessage());
//            throw new RuntimeException("File retrieval error", e);
//        }
//    }
//
//    @Override
//    public void delete(String providerKey) {
//        // ToDo, this will be schcuduled to delete user data as we cannot keep it forever auth Jack
//    }
//
//    @Override
//    public String getProviderName() {
//        return "MINIO";
//    }
//}