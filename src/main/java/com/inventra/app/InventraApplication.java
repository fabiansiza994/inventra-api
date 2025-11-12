package com.inventra.app;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventraApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventraApplication.class, args);
	}

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.getConfiguration()
                .setPropertyCondition(context ->
                        !(context.getSource() instanceof PersistentCollection) ||
                                ((PersistentCollection) context.getSource()).wasInitialized());

        return modelMapper;
    }

}
