package com.epam.input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Input {
    private static final Logger LOG = LogManager.getLogger(Input.class);

    public static int getNumber(){
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int number = Integer.parseInt(reader.readLine());
                LOG.info(String.format("Return int:%d",number));
                return number;
            } catch (IOException ex){
                LOG.warn("Couldn't read input from user.");
            }catch (StringIndexOutOfBoundsException ex){
                LOG.warn(ex.getMessage());
            }
        }
    }
}
