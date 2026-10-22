package za.co.fraud.fraud_service.services.storage;
import java.io.InputStream;

public interface StorageProvider {
    String upload(InputStream inputStream, long fileSize, String path, String fileName, String contentType);
    InputStream download(String providerKey);
    void delete(String providerKey);
    String getProviderName();
}
