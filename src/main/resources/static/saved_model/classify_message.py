from flask import Flask, request, jsonify
import tensorflow as tf

app = Flask(__name__)

# Carica il modello TensorFlow
model = tf.keras.models.load_model('/home/lorenzoscaf/saved_model-20230717T105537Z-001/saved_model')

@app.route('/classify', methods=['POST'])
def classify_message():
    # Ottieni il messaggio dalla richiesta POST
    message = request.json['message']

    # Esegui la classificazione del messaggio
    probability = model.predict([message])[0][0]

    # Restituisci il risultato come risposta JSON
    return jsonify({'probability': float(probability)})

if __name__ == '__main__':
    app.run()

