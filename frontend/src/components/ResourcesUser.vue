<template>
  <v-data-table
      :headers="headers"
      :search="search"
      :items="resources"
      sort-by="resourceName"
      item-key="resourceName"
  >
    <template v-slot:[`item.timer`] = "{ item }">
      <h2 date='item.timer'>{{item.timer}} {{days}} d {{hours}} h {{minutes}} m {{seconds}} s</h2>
    </template>


    <template v-slot:top>
      <v-text-field
          v-model="search"
          append-icon="mdi-magnify"
          label="Search"
          class="mx-10"
      ></v-text-field>
      <v-dialog
          v-model="dialog"
          max-width="500px"
      >

        <v-card>
          <v-card-title>
            <span class="headline">Book this resource</span>
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-form ref="form">
                <v-text-field
                    v-model="reason"
                    label="Reason of reserving the resource"
                ></v-text-field>
                <v-text-field
                    v-model="time"
                    label="Estimated reservation time"
                ></v-text-field>
                <v-checkbox
                    v-model="notify"
                    label="Keep me notified"
                ></v-checkbox>
              </v-form>

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
                @click="book"
            >
              Book
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-dialog v-model="showHistDialog">
        <v-card>
          <v-card-title>
            <span class="headline">Resource Booking History</span>
          </v-card-title>

          <v-card-text>
            <v-data-table
                :headers="historyHeaders"
                :items="historyRows"
            >
            </v-data-table>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                color="blue darken-1"
                text
                @click="closeHistory"
            >
              Cancel
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>


    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon v-if= "role === 'EDIT'"
              @click="bookItem(item)"
      >
        mdi-calendar-edit
      </v-icon>
      <v-icon v-else
              @click="bookItem(item)"
              disabled
      >
        mdi-calendar-edit
      </v-icon>

      <v-icon
          @click="showHistory(item)"
      >
        mdi-history
      </v-icon>
    </template>
    <template v-slot:no-data>
      <v-btn
          color="primary"
          @click="getUsers"
      >
        Reset
      </v-btn>
    </template>
  </v-data-table>
</template>

<script>
export default {
  name: "ResourcesUser.vue",

  two_digits: (value) => {
    if (value < 0) {
      return '00';
    }
    if (value.toString().length <= 1) {
      return `0${value}`;
    }
    return value;
  },

  data: () => ({
    reason: '',
    time: '',
    notify: '',

    search: '',
    dialog: false,
    dialogDelete: false,
    showHistDialog: false,

    expanded: [],
    singleExpand: true,

    drawer: false,
    group: null,
    countSpaces: 2,
    now: Math.trunc((new Date()).getTime() / 1000),

    editedIndex: -1,
    editedItem: {
      resourceName: '',
      resourceDescription: '',
      resourceType: '',
      resourceState: ''
    },
    defaultItem: {
      resourceName: '',
      resourceDescription: '',
      resourceType: '',
      resourceState: ''
    },

    date: '',

    headers: [
      { text: 'Resource name', value: 'resourceName', align: 'start', sortable: true },
      { text: 'Description', value: 'resourceDescription' },
      { text: 'Type', value: 'resourceType' },
      { text: 'State', value: 'resourceState' },
      { text: 'Actions', value: 'actions', sortable: false }
    ],

    resources: [
      { resourceName: "Resource 1", resourceDescription: "Description 1", resourceType: "ROOM", resourceState: "Available"},
      { resourceName: "Resource 2", resourceDescription: "Description 2", resourceType: "ROOM", resourceState: "Available"},
      { resourceName: "Resource 3", resourceDescription: "Description 3", resourceType: "ROOM", resourceState: "Available"},
      { resourceName: "Resource 4", resourceDescription: "Description 4", resourceType: "ROOM", resourceState: "Available"},
      { resourceName: "Resource 5", resourceDescription: "Description 5", resourceType: "ROOM", resourceState: "Available"},
    ],


    historyHeaders: [
      {text: 'First name', value: 'firstName', sortable: true},
      {text: 'Last name', value: 'lastName', sortable: true},
      {text: 'Booked at', value: 'fromBookingTime', sortable: true},
      {text: 'Booked by', value: 'toBookingTime', sortable: true}
    ],
    historyRows: [
      {firstName: 'Andrei', lastName: 'Popescu', fromBookingTime: "B", toBookingTime: 'C'},
      {firstName: 'Andrei', lastName: 'Popescu', fromBookingTime: "B", toBookingTime: 'C'},
      {firstName: 'Andrei', lastName: 'Popescu', fromBookingTime: "B", toBookingTime: 'C'},
      {firstName: 'Andrei', lastName: 'Popescu', fromBookingTime: "B", toBookingTime: 'C'},
    ],
  }),
  props: {
    dateTimer: {
      type: String
    }
  },
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Book this resource'
    },

    dateInMilliseconds() {
      return Math.trunc(Date.parse(this.date) / 1000)
    },
    seconds() {
      return (this.dateInMilliseconds - this.now) % 60;
    },
    minutes() {
      return Math.trunc((this.dateInMilliseconds - this.now) / 60) % 60;
    },
    hours() {
      return Math.trunc((this.dateInMilliseconds - this.now) / 60 / 60) % 24;
    },
    days() {
      return Math.trunc((this.dateInMilliseconds - this.now) / 60 / 60 / 24);
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
  mounted: function() {
    window.setInterval(() => {
      this.now = Math.trunc((new Date()).getTime() / 1000);
    },1000);
  },

  methods: {
    bookItem(item) {
      this.editedIndex = this.resources.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    close () {
      this.dialog = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },
    book () {
      // here will send data to backend
    },

    showHistory(item) {
      console.log(item);
      //this.editedIndex = this.resources.indexOf(item)
      //this.editedItem = Object.assign({}, item)
      this.showHistDialog = true;
    },

    closeHistory(){
      this.showHistDialog = false;
    },
  }
}
</script>

<style scoped>

</style>