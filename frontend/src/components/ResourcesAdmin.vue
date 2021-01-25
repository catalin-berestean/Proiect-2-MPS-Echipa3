<template>
  <v-data-table
    :headers="headers"
    :items="resources"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar
        flat
      >
        <v-spacer></v-spacer>
        <v-dialog
          v-model="dialog"
          max-width="500px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="primary"
              dark
              class="mb-2"
              v-bind="attrs"
              v-on="on"
            >
              New Resource
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.name"
                      label="Resource name"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.description"
                      label="Description"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.type"
                      label="Resource type"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
              <v-alert
                  dense
                  v-show="display_error"
                  type="error"
                  elevation="4"
                  dismissible
              >
                {{ error_message }}
              </v-alert>
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
        
      </v-toolbar>
    </template>
    
    <template v-slot:no-data>
      <v-btn
        color="primary"
        @click="getResources"
      >
        Reset
      </v-btn>
    </template>
  </v-data-table>
</template>

<script>
import axios from "axios";

const API_PATH = "http://localhost:8081/api";
export default {
  name: "ResourcesAdmin.vue",
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'Create new resource' : 'Edit items'
    }
  },
  data: () => ({
    dialog: false,
    dialogDelete: false,
    display_error: false,
    error_message: '',
    countSpaces: 3,

    watch: {
      dialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
    },

    headers: [
      { text: 'Name', value: 'name', align: 'start', sortable: true },
      { text: 'Description', value: 'description', sortable: true },
      { text: 'Type', value: 'type', sortable: true }
    ],

    resources: [],

    editedIndex: -1,
      editedItem: {
        name: '',
        description: '',
        type: '',
      },
      defaultItem: {
        name: '',
        description: '',
        type: '',
      },


  }),
  mounted() {
    this.getResources()
  },
  methods: {
    getResources() {
        axios.get(API_PATH + "/resources")
            .then(response => {
              this.resources = response.data;
            })
    },
    addNewResource() {
      axios
          .post(API_PATH + "/resources", {
            name: this.editedItem.name,
            description: this.editedItem.description,
            type: this.editedItem.type
          })
          .then((res) => {
            console.log(res);
            this.error_message = false;
            this.resources = this.getResources();
            this.close();
          })
          .catch((error) => {
            let msg = error.response.data["message"]
            if(!msg) {
              this.error_message = "Error processing the request";
            } else {
              this.error_message = msg;
            }
            this.display_error = true;
          });
    },

    editItem (item) {
      this.editedIndex = this.resources.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    deleteItem (item) {
      this.editedIndex = this.resources.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialogDelete = true
    },

    deleteItemConfirm () {
      this.resources.splice(this.editedIndex, 1)
      this.closeDelete()
    },

    close () {
      this.dialog = false
      this.display_error = false;
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
      this.addNewResource();
    },
  }
}
</script>

<style scoped>

</style>