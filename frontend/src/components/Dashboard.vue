<template>
    <v-container>
        <v-row justify="center">
            <div>
                <h1>Welcome, {{ username }} !</h1>
            </div>
        </v-row>
        

        
        <v-card-text>
            <div v-if = "username === 'admin'">
                <v-card>
                    <v-tabs
                    background-color="deep-purple accent-4"
                    center-active
                    fixed-tabs
                    dark
                    >
                        <v-tab>My users</v-tab>
                        <v-tab>Resources</v-tab>
                        
                        <v-tab-item>
                            <v-data-table
                                :headers="headers"
                                :items="users"
                                sort-by="name"
                                class="elevation-1"
                            >
                                <template v-slot:top>
                                <v-toolbar flat >
                                    
                                    <v-dialog
                                    v-model="dialog"
                                    max-width="500px"
                                    >
                                    
                                    <v-card>
                                        <v-card-title>
                                            <span class="headline">{{ formTitle }}</span>
                                        </v-card-title>

                                        <v-card-text>
                                            <v-container>
                                                <v-row>
                                                    <v-col
                                                        cols="12"
                                                        sm="6"
                                                        md="4"
                                                    >
                                                        <v-text-field
                                                            v-model="editedItem.name"
                                                            label="User name"
                                                        ></v-text-field>
                                                    </v-col>
                                                    <v-col
                                                        cols="12"
                                                        sm="6"
                                                        md="4"
                                                    >
                                                        <v-select
                                                            :items="permissions"
                                                            item-text="role"
                                                            item-value="role"
                                                            label="Permission"
                                                            persistent-hint
                                                            return-object
                                                            single-line
                                                        ></v-select>
                                                    </v-col>
                                                </v-row>
                                            </v-container>
                                        </v-card-text>

                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn
                                                color="blue darken-1"
                                                text
                                                @click="close"
                                            >
                                                Cancel
                                            </v-btn>
                                            <v-btn
                                                color="blue darken-1"
                                                text
                                                @click="save"
                                            >
                                                Save
                                            </v-btn>
                                        </v-card-actions>
                                    </v-card>
                                    </v-dialog>
                                    <v-dialog v-model="dialogDelete" max-width="500px">
                                    <v-card>
                                        <v-card-title class="headline">Are you sure you want to delete this item?</v-card-title>
                                        <v-card-actions>
                                        <v-spacer></v-spacer>
                                        <v-btn color="blue darken-1" text @click="closeDelete">Cancel</v-btn>
                                        <v-btn color="blue darken-1" text @click="deleteItemConfirm">OK</v-btn>
                                        <v-spacer></v-spacer>
                                        </v-card-actions>
                                    </v-card>
                                    </v-dialog>
                                </v-toolbar>
                                </template>
                                <template v-slot:[`item.actions`]="{ item }">
                                    <v-icon
                                        small
                                        class="mr-2"
                                        @click="editItem(item)"
                                    >
                                        mdi-pencil
                                    </v-icon>
                                    <v-icon
                                        small
                                        @click="deleteItem(item)"
                                    >
                                        mdi-delete
                                    </v-icon>
                                    </template>
                                    <template v-slot:no-data>
                                    <v-btn
                                        color="primary"
                                        @click="initialize"
                                    >
                                        Reset
                                    </v-btn>
                                </template>
                            </v-data-table>
                        </v-tab-item>


                        <v-tab-item>
                            text 2
                        </v-tab-item>
                    </v-tabs>
                </v-card>
                <v-spacer></v-spacer>
                <v-row>
                    <v-col align="center">
                        <v-btn
                            large
                            raised
                            rounded
                            color="primary"
                            @click="logout"
                            >Logout</v-btn>
                    </v-col>
                </v-row>  
            </div>
            <div v-else>
                <v-card>
                    <h1>User normal</h1>
                </v-card>                

                <v-row align="center" justify="center">
                    <v-col align="center">
                        <v-btn
                            large
                            raised
                            rounded
                            color="primary"
                            @click="logout"
                            >Logout</v-btn>
                    </v-col>
                </v-row>           
            </div>  
        </v-card-text>
        
        
    </v-container>
</template>

<script>

export default {
    name: 'Dashboard',
        
    computed: {
        formTitle () {
            return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
        }
    },

    watch: {
      dialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
    },

    methods: {
        saveChanges() {
            // Save users (new) permissions
        },

        logout() {
            this.$router.push({ name: 'Login' });
        },

        editItem (item) {
            this.editedIndex = this.users.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem (item) {
            this.editedIndex = this.users.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialogDelete = true
        },

        deleteItemConfirm () {
            this.users.splice(this.editedIndex, 1)
            this.closeDelete()
        },

        close () {
            this.dialog = false
            this.$nextTick(() => {
            this.editedItem = Object.assign({}, this.defaultItem)
            this.editedIndex = -1
            })
        },

        closeDelete () {
            this.dialogDelete = false
            this.$nextTick(() => {
            this.editedItem = Object.assign({}, this.defaultItem)
            this.editedIndex = -1
            })
        },

        save () {
            if (this.editedIndex > -1) {
            Object.assign(this.users[this.editedIndex], this.editedItem)
            } else {
            this.users.push(this.editedItem)
            }
            this.close()
        }
    },

    data: () => ({
        countSpaces: 3,
        username: '',
        dialog: false,
        dialogDelete: false,

        permissions: [
            {role: 'VIEW'},
            {role: 'EDIT'}
        ],

        headers: [
            { text: 'Username', value: 'name', align: 'start', sortable: true }, 
            { text: 'Permission', value: 'role' },
            { text: 'Actions', value: 'actions', sortable: false }
        ],

        users: [
            {name: 'user1', role: 'vizualizare'},
            {name: 'user2', role: 'vizualizare'},
            {name: 'user3', role: 'rezervare'},
            {name: 'user4', role: 'vizualizare'},
            {name: 'user3', role: 'rezervare'},
            {name: 'user3', role: 'rezervare'},
            {name: 'user3', role: 'rezervare'},
            {name: 'user3', role: 'rezervare'},
            {name: 'user3', role: 'rezervare'}
        ],

        editedIndex: -1,
        editedItem: {
            name: '',
            role: ''
        },
        defaultItem: {
            name: '',
            role: ''
        },

    }),

    mounted: function() {
        if (localStorage.getItem('username')) {
            try {
                this.username = localStorage.getItem('username');
            } catch(e) {
                localStorage.removeItem('username');
            }
        }
    },

    created(){

    }
}
</script>

<style>



</style>