package com.example.mypizza;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    private Button btn_calcular;
    private TextView txtPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //referência dos widgets
        spinner = findViewById(R.id.spinner);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        btn_calcular = findViewById(R.id.btn_calcular);
        txtPedido = findViewById(R.id.txtPedido);
        configurarTamanhos();
        configurarEscolhas();
        btn_calcular.setOnClickListener(view -> calcularPedido());
    }

    //serve para adicionar as opções de tamanho no spinner
    private void configurarTamanhos() {
        String opcoesTamanho[] = {"Grande (8 fatias, até 2 sabores)", "Família (12 fatias, até 3 sabores)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcoesTamanho);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //serve para configurar os checkboxes para controlar a quantidade máxima de sabores
    private void configurarEscolhas() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                controlarQuantidadeSabores();
            }
        };

        checkBox1.setOnCheckedChangeListener(listener);
        checkBox2.setOnCheckedChangeListener(listener);
        checkBox3.setOnCheckedChangeListener(listener);
        checkBox4.setOnCheckedChangeListener(listener);
        checkBox5.setOnCheckedChangeListener(listener);
    }

    //serve para controlar a quantidade máxima de sabores que podem ser escolhidos
    private void controlarQuantidadeSabores() {
        int maximoSabores = obterMaximoSabores();
        int saboresEscolhidos = obterSaboresEscolhidos().size();

        //se ultrapassar a quantidade de sabores, desmarca o último sabor escolhido
        if (saboresEscolhidos > maximoSabores) {
            CheckBox checkboxes[] = {checkBox1, checkBox2, checkBox3, checkBox4, checkBox5};
            for (int i = checkboxes.length - 1; i >= 0; i--) {
                if (checkboxes[i].isChecked()) {
                    checkboxes[i].setChecked(false);
                    Toast.makeText(this, "Limite de sabores ultrapassado!! >_<", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    //serve para mostrar a quantidade máxima de sabores permitido conforme o tamanho escolhido
    private int obterMaximoSabores() {
        String tamanhoSelecionado = spinner.getSelectedItem().toString();
        return tamanhoSelecionado.contains("Grande") ? 2 : 3;
    }

    //serve para mostar os sabores escolhidos
    private List<String> obterSaboresEscolhidos() {
        List<String> sabores = new ArrayList<>();
        if(checkBox1.isChecked()) sabores.add("Calabresa");
        if(checkBox2.isChecked()) sabores.add("Portuguesa");
        if(checkBox3.isChecked()) sabores.add("Morango com Nutella");
        if(checkBox4.isChecked()) sabores.add("Frango com palmito");
        if(checkBox5.isChecked()) sabores.add("Brigadeiro");
        return sabores;
    }

    //serve para calcular o valor total do pedido e confirmar pedido
    private void calcularPedido()
    {
        List<String> sabores = new ArrayList<>();
        if(checkBox1.isChecked()) sabores.add("Calabresa");
        if(checkBox2.isChecked()) sabores.add("Portuguesa");
        if(checkBox3.isChecked()) sabores.add("Morango com Nutella");
        if(checkBox4.isChecked()) sabores.add("Frango com palmito");
        if(checkBox5.isChecked()) sabores.add("Brigadeiro");

        //serve para verificar se algum sabor foi escolhido
        if(sabores.isEmpty())
        {
            new AlertDialog.Builder(this).setTitle("ATENÇÃO!!").setMessage("Escolha pelo menos um sabor.").setPositiveButton("OK", null).show();
            return;
        }

        //serve para calcular o preço total
        final double total = calcularPreco(sabores);
        final String tamanho = spinner.getSelectedItem().toString();

        //Serve para exibir uma janela de confirmação
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Tamanho: ").append(tamanho).append("\n\n").append("Sabores:\n");

        for(String sabor : sabores)
        {
            mensagem.append("- ").append(sabor).append("\n");
        }
        mensagem.append("\nValor Total: R$").append(String.format("%.2f", total));

        new AlertDialog.Builder(this).setTitle("Confirmação do Pedido").setMessage(mensagem.toString())
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    StringBuilder detalhes = new StringBuilder();
                    detalhes.append("PEDIDO CONFIRMADO!\n\n").append("Tamanho: ").append(tamanho).append("\n").append("Sabores: ");

                    for(int i = 0; i < sabores.size(); i++)
                    {
                        if(i > 0) detalhes.append(", ");
                        detalhes.append(sabores.get(i));
                    }

                    detalhes.append("\nValor Total: R$ ").append(String.format("%.2f", total));
                    txtPedido.setText(detalhes.toString());
                }).setNegativeButton("Voltar", null).show();
    }

    private double calcularPreco(List<String> sabores)
    {
        double total = 0;

        for(String sabor : sabores)
        {
            total += sabor.equals("Morango com Nutella") ? 30.00 : 20.00;
        }
        return total;
    }
}