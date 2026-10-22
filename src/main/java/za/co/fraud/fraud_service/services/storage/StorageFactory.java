package za.co.fraud.fraud_service.services.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StorageFactory {

    private final Map<String, StorageProvider> providers;

    @Value("${storage.type}")
    private String activeProviderType;

    public StorageFactory(List<StorageProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        provider -> provider.getProviderName().toUpperCase(),
                        provider -> provider
                ));
    }

    public StorageProvider getActiveProvider() {
        return getProvider(activeProviderType);
    }

    public StorageProvider getProvider(String providerType) {
        StorageProvider provider = providers.get(providerType.toUpperCase());
        if (provider == null) {
            throw new IllegalArgumentException("No storage provider found for type: " + providerType);
        }
        return provider;
    }
}
