package br.com.psousa.findeasy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.util.AndroidUtils;

public class ViewEmailRecuperaSenha extends Fragment {


    public ViewEmailRecuperaSenha() {
        // Required empty public constructor
    }

    private EditText mEdtEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_email_recupera_senha, container, false);

        mEdtEmail = (EditText) view.findViewById(R.id.edt_recupera_senha_email);

        return view;
    }

    public String getEmail(){

        if (mEdtEmail == null ||
                mEdtEmail.getText().toString().isEmpty()){
            AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.informe_email));
            return "";
        } else {
            if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(mEdtEmail.getText().toString().trim()).matches() && mEdtEmail.getText().toString().trim().length() > 0)) {
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.email_invalido));
                return "";
            }
        }

        if (mEdtEmail != null){
            return mEdtEmail.getText().toString();
        } else {
            return "";
        }
    }


}
