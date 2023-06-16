const express = require('express');
const app = express();
const multer = require('multer');
const port = parseInt(process.env.PORT) || 5000;
const path = require('path');
const sharp = require('sharp');
const fs = require('fs');
const cron = require('node-cron');
const tf = require('@tensorflow/tfjs-node');
const picturesFolderPath = './pictures';

let model; // Variable to store the loaded model


// Define the cron schedule
cron.schedule('0 * * * *', () => {
    // Delete files inside the pictures folder
    fs.readdir(picturesFolderPath, (err, files) => {
      if (err) {
        console.error('Error reading directory:', err);
        return;
      }
  
      // Delete each file
      files.forEach((file) => {
        const filePath = path.join(picturesFolderPath, file);
  
        fs.unlink(filePath, (err) => {
          if (err) {
            console.error('Error deleting file:', err);
          } else {
            console.log(`Deleted: ${filePath}`);
          }
        });
      });
    });
});

// Load the machine learning model
async function loadModel() {
  try {
    model = await tf.loadLayersModel('https://storage.googleapis.com/waste-classification-model-90/model.json');
    console.log('Model loaded successfully');
  } catch (error) {
    console.error('Error loading the model:', error);
    throw error;
  }
}

// Preprocess the image before prediction
async function preprocessImage(imagePath) {
  try {
    const image = await sharp(imagePath)
      .resize(224, 224)
      .normalize()
      .toBuffer();

    const input = tf.node.decodeImage(image, 3);
    const expandedInput = input.expandDims();
    return expandedInput;
  } catch (error) {
    console.error('Error preprocessing the image:', error);
    throw error;
  }
}

// Perform image classification prediction
async function predictPic(imagePath) {
  try {
    if (!model) {
      // Load the model if it is not already loaded
      await loadModel();
    }

    const inputTensor = await preprocessImage(imagePath);
    const predictions = await model.predict(inputTensor);

    const dataArray = predictions.arraySync()[0];

    const confidence = {};
    for (let i = 0; i < dataArray.length; i++) {
      confidence[i] = dataArray[i];
    }

    let maxConfidence = -1;
    let predictedClass = -1;

    // Find the predicted class with the highest confidence
    Object.entries(confidence).forEach(([classIndex, classConfidence]) => {
      if (classConfidence > maxConfidence) {
        maxConfidence = classConfidence;
        predictedClass = parseInt(classIndex);
      }
    });

    let predictedLabel = "";
    let description = "";
    let handling = "";
    if (predictedClass === 0) {
      predictedLabel = "Organic Waste";
      description = "Organic waste refers to waste materials that come from living organisms or contain organic compounds. It includes things like food scraps, yard trimmings, agricultural residues, and paper products. Organic waste is biodegradable, meaning it can be broken down naturally by microorganisms into simpler substances. Proper management of organic waste through methods like composting or anaerobic digestion can turn it into valuable resources like nutrient-rich soil amendments or renewable energy sources. By diverting organic waste from landfills and utilizing it effectively, we can reduce greenhouse gas emissions, support sustainable practices, and contribute to a circular economy.";
      handling = "Green trash bins are designated for organic waste, so you can dispose of this type of organic waste in the green garbage cans. Besides that, there are several options to manage the organic waste in the following ways :\n\n1. Composting : Composting is a simple and effective way to handle organic waste. You can create a compost pile or use a compost bin in your backyard or garden. By collecting food scraps, yard trimmings, and other organic materials, you can create nutrient-rich compost that can be used to fertilize plants and gardens. Collaborate with neighbors, local organizations, or waste management agencies to establish shared composting facilities in your community. This can help optimize resource utilization and promote a circular economy.\n2. Home Vermicomposting : Vermicomposting is an excellent option for smaller living spaces, such as apartments or houses with limited outdoor areas. You can set up a worm composting bin indoors using special composting worms (such as red wigglers) to break down organic waste. The resulting worm castings can be used as a nutrient-rich soil amendment for houseplants or balcony gardens.\n3. Farmers Markets and Organic Farms : Some farmers markets or organic farms in Indonesia may accept organic waste for composting purposes. These organizations often have their own composting systems and can make good use of additional organic materials. Reach out to local farmers markets or organic farms to inquire if they accept organic waste donations.\n4. Biogas Production : Consider setting up a biogas system for larger organic waste volumes. Biogas is produced through anaerobic digestion of organic waste and can be used as a renewable energy source for cooking or electricity generation.\nRemember, it is important to separate organic waste from other types of waste, such as plastics or metals, to ensure proper recycling and disposal. By responsibly managing your organic waste, you can contribute to reducing the overall waste burden, promoting sustainable practices, and nurturing a healthier environment in Indonesia!";
    } else if (predictedClass === 1) {
      predictedLabel = "Inorganic Waste";
      description = "Inorganic waste refers to waste materials that are not derived from living organisms and do not contain organic compounds. It includes non-biodegradable materials such as metals, glass, plastic, ceramics, and synthetic chemicals. Unlike organic waste, inorganic waste does not break down naturally or decompose easily. These materials can persist in the environment for long periods, leading to pollution and potential harm to ecosystems. Effective management of inorganic waste involves practices like recycling, reusing, and proper disposal methods such as landfilling or incineration. By minimizing the generation of inorganic waste and adopting sustainable waste management strategies, we can reduce the environmental impact and promote resource conservation.";
      handling = "Yellow trash bins are designated for inorganic waste, so you can dispose of this type of waste in the yellow garbage cans. In addition, you can manage inorganic waste in the following ways :\n\n1. Recycling : Identify recyclable materials within your inorganic waste, such as plastic bottles, paper, cardboard, glass, and metals. Check with local recycling centers or waste management companies to find out where you can deliver these materials for recycling. Support local recycling initiatives and encourage others to do the same.\n2. Electronic Waste Collection: For electronic waste, including old appliances, computers, and mobile phones, it is important to dispose of them properly to prevent environmental contamination. Look for e-waste collection points or authorized electronic waste recycling companies in your area. They specialize in handling electronic waste and ensure that hazardous components are managed safely.\n3. Donations and Reuse: Consider donating or reusing items that are still in good condition instead of throwing them away. For example, you can repurpose glass jars as storage containers or use old newspapers for wrapping items. Donate or sell items that are still in good condition to extend their lifespan. This approach helps reduce waste and extends the lifespan of usable items.\nRemember, it is essential to separate inorganic waste from organic waste to ensure proper recycling and disposal. Additionally, practicing waste reduction and conscious consumption by minimizing the use of single-use plastics and packaging can significantly contribute to reducing the volume of inorganic waste generated.";
    } else if (predictedClass === 2) {
      predictedLabel = "Hazardous Waste";
      description = "Hazardous waste refers to waste materials that possess properties that make them potentially harmful to human health, the environment, or both. It includes substances that are toxic, flammable, corrosive, reactive, or infectious. Hazardous waste can come from various sources such as industrial processes, healthcare facilities, laboratories, and households. The improper handling, storage, or disposal of hazardous waste can lead to serious health risks and environmental contamination. These wastes require special attention and specific management procedures to ensure their safe handling and minimize their impact.";
      handling = "Red trash bins are designated for inorganic waste, so you can dispose of this type of waste in the red garbage cans. Besides that, you can manage hazardous waste in the following ways :\n\n1. Storage: Store hazardous waste in secure and appropriate containers that are resistant to leaks or spills. Ensure that storage areas are well-maintained, properly ventilated, and equipped with necessary safety measures. Regularly inspect and monitor the storage facilities to prevent accidents.\n2. Transportation: Hazardous waste must be transported by licensed carriers who comply with transportation regulations. Ensure that the waste is packaged securely and labeled correctly during transport to prevent any leakage or spills.\n3. Treatment and disposal: Hazardous waste treatment and disposal should be carried out at authorized facilities. This may involve various methods such as incineration, chemical treatment, or recycling. Choose facilities that meet environmental standards and possess the necessary permits for handling hazardous waste.\n4. Record-keeping and reporting: Maintain accurate records of the generation, storage, transportation, treatment, and disposal of hazardous waste. Submit required reports to the relevant regulatory authorities as per their guidelines and timelines.\nRemember, hazardous waste should never be mixed with regular household waste or poured down drains, as it can contaminate the environment and pose risks to human health. Always prioritize the responsible disposal of hazardous waste to protect both the environment and the well-being of communities in Indonesia.";
    }

    return { prediction: predictedLabel, confidence, description, handling };
  } catch (error) {
    console.error('Error predicting the image:', error);
    throw error;
  }
}

// Configure multer storage for file uploads
const storage = multer.diskStorage({
  destination: './pictures',
  filename: (req, file, cb) => {
    return cb(null, `${file.fieldname}_${Date.now()}${path.extname(file.originalname)}`);
  }
});

const upload = multer({ storage });

app.use('/result', express.static('pictures'));

// Handle the upload image endpoint
app.post("/upload", upload.single('data'), async (req, res) => {
  try {
    const imagePath = `./pictures/${req.file.filename}`;
    const last_predict = await predictPic(imagePath);
    const another_data = {
      success: true,
    };

    let last_response = { ...last_predict, ...another_data, accuracy: last_predict.accuracy };
    res.json(last_response);

    // Delete the uploaded image
    fs.unlink(imagePath, (err) => {
        if (err) {
          console.error('Error deleting file:', err);
        } else {
          console.log(`Deleted: ${imagePath}`);
        }
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'An error occurred' });
  }
});

// Handle the root endpoint
app.get('/', function (req, res) {
  res.status(200).send('Connected').end();
});

// Load the model on server start
loadModel().then(() => {
  // Start the server
  app.listen(port, () => {
    console.log("Server started on port 5000");
  });
});
