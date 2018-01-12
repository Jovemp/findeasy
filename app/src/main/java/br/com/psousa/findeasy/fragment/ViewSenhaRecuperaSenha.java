package br.com.psousa.findeasy.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.util.AndroidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSenhaRecuperaSenha extends Fragment {


    public ViewSenhaRecuperaSenha() {
        // Required empty public constructor
    }

    private EditText mEdtSenha;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_senha_recupera_senha, container, false);

        mEdtSenha = (EditText) view.findViewById(R.id.edt_recupera_senha_senha);

        return view;
    }

    public String getSenha(){
        if (mEdtSenha == null ||
                mEdtSenha.getText().toString().isEmpty()){
            AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.informe_senha));
            return "";
        } else {
            if (!(mEdtSenha.getText().toString().trim().length() > 5)) {
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.senha_curta));
                return "";
            }
        }

        if (mEdtSenha != null){
            return mEdtSenha.getText().toString();
        } else {
            return "";
        }
    }

}
