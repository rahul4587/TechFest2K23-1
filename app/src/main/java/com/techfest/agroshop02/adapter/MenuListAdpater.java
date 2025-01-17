package com.techfest.agroshop02.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.EachMenuItemBinding;
import com.techfest.agroshop02.listeners.MenuItemListners;

import java.util.List;

import Models.FarmersModel;
import Models.MenuItem;
import Models.PreferanceManager;

public class MenuListAdpater extends RecyclerView.Adapter<MenuListAdpater.MenuViewHolder> {
    int quentity = 0;
    private final List<MenuItem> menuItems;
    private final MenuItemListners menuItemListners;
    PreferanceManager preferanceManager;

    public MenuListAdpater(List<MenuItem> menuItems, MenuItemListners menuItemListners) {
        this.menuItems = menuItems;
        this.menuItemListners = menuItemListners;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     EachMenuItemBinding eachMenuItemBinding=EachMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

     return new MenuViewHolder(eachMenuItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MenuListAdpater.MenuViewHolder holder, int position) {
holder.setData(menuItems.get(position));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }




    public class MenuViewHolder extends RecyclerView.ViewHolder {
        EachMenuItemBinding binding;

        public MenuViewHolder(EachMenuItemBinding eachMenuItemBinding) {

            super(eachMenuItemBinding.getRoot());
            binding=eachMenuItemBinding;
        }

        public void setData(MenuItem menuItem) {
            preferanceManager=new PreferanceManager(itemView.getContext());
            binding.productName.setText(menuItem.productName);
            quentity=0;
            Picasso.get().load(menuItem.productImage).into(binding.PoductImage);

            binding.poductNegetive.setOnClickListener(view -> {
               if(quentity>0) {
                    quentity--;
                }
                binding.productCount.setText(String.valueOf(quentity));
            });
            binding.productPosittive.setOnClickListener(view -> {
              quentity++;

                binding.productCount.setText(String.valueOf(quentity));
               });

            binding.productDescription.setText(menuItem.productDesciption);
            binding.productDate.setText(menuItem.productdate);
            binding.productAmount.setText("Rs."+menuItem.productPrice+"/TON");
            binding.moreAbout.setOnClickListener(v->{
                preferanceManager.putString(FarmersModel.ORDERID,menuItem.productId);
                menuItemListners.onItemClicked(menuItem);});
            checkDesignation(menuItem);
           

        }

        private void getFarmerDetails() {


        }

        private void checkDesignation(MenuItem menuItem) {
     if(preferanceManager.getString(FarmersModel.KEY_DESIGNATION).matches("Farmer")){
         Log.d("ProductStatus",menuItem.productStatus);

         if(menuItem.productStatus.matches("0")){
                 binding.checkout.setText("Available");
                 binding.checkout.setBackgroundColor(Color.GREEN);
                 binding.checkout.setVisibility(View.VISIBLE);
             }
             else if(menuItem.productStatus.matches("1")){
                 binding.checkout.setBackgroundColor(Color.RED);
                 binding.checkout.setText("UnAvailable");
                 binding.checkout.setVisibility(View.VISIBLE);
             }

             final int[] status = {0};

       binding.checkout.setOnClickListener(view -> {
           if(menuItem.personDesignation.matches("Farmer")&&(menuItem.productStatus.matches("1")))
           {

               binding.checkout.setText("Available");
               preferanceManager.putString(FarmersModel.KEY_ITEM_STATUS,"Available");
               status[0] +=1;
               binding.checkout.setBackgroundColor(Color.GREEN);
               FirebaseFirestore  firebaseFirestore=FirebaseFirestore.getInstance();
               firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION).document(menuItem.productId).update(FarmersModel.KEY_ITEM_STATUS,"0");

           }else if(menuItem.personDesignation.matches("Farmer")&&(menuItem.productStatus.matches("0")))
           {
               binding.checkout.setText("UnAvailable");
               status[0] -=1;
               preferanceManager.putString(FarmersModel.KEY_ITEM_STATUS,"UnAvailable");
               binding.checkout.setBackgroundColor(Color.RED);
               FirebaseFirestore  firebaseFirestore=FirebaseFirestore.getInstance();
               firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION).document(menuItem.productId).update(FarmersModel.KEY_ITEM_STATUS,"1");
           }
       });

     }
     else if (preferanceManager.getString(FarmersModel.KEY_DESIGNATION).matches("Distributor")) {
         binding.checkout.setVisibility(View.VISIBLE);
         binding.AddingId.setVisibility(View.VISIBLE);
         binding.checkout.setOnClickListener(view -> {
             menuItemListners.onItemSelected(menuItem,quentity,menuItem.productName,menuItem.productPrice,menuItem.productId);
         });


     }
        }



    }
}




